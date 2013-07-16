import tweepy, sys, os
import argparse # requires 2.7
import random, re, daemon, lockfile
from pymongo import MongoClient
import json

diere = re.compile('(\d+)[dD](\d+)([+-]?\d*)')
useraccount = "@dicenessapp"
db_collection = None

def handle_command_line():
    parser = argparse.ArgumentParser(description="Run streaming twitter bot")
    parser.add_argument("-t", "--test", help="Get a test response")
    parser.add_argument("-k", "--keyfile", help="Twitter account consumer and accesstokens")
    parser.add_argument("-u", "--username", help="Username to follow", default="@dicenessapp")
    args = parser.parse_args()
    return args


class Dice:
    def __init__(self,mth):
        self.multiplier = int(mth[0])
        self.type = int(mth[1])
        if mth[2] == '':
            self.modifier = 0
        else:
            self.modifier = int(mth[2])

    def mod_str(self):
        if self.modifier != 0:
            return "%+d" % self.modifier
        else:
            return ""

    def __repr__(self):
        return "%dd%d%s" % (self.multiplier,self.type,self.mod_str())

    def roll(self):
        r = random.Random()
        val = []
        for i in xrange(self.multiplier):
            val.append(r.randint(1,self.type))
        self.lastRoll = sum(val) + self.modifier
        self.lastRollStr = "Rolled %d using %s.\n Individual dice: (%s%s).\nOur roll number %d." % (self.lastRoll, repr(self), "+".join(map(str,val)), self.mod_str(), self.counter)


class DiceParser:
    def __init__(self,description,prev):
        mth = diere.match(description)
        if not mth:
             mth = diere.match(prev)
        if mth:
            self.die = Dice(mth.groups())
        else:
            self.die = Dice([1,6,0])

class ReplyGenerator:
    def __init__(self,tweet,prev,counter):
        self.tweet = tweet
        self.prev = prev
        dp = DiceParser(tweet.split()[1],prev)
        self.dice = dp.die
        self.dice.counter = counter
        self.dice.roll()

    def reply(self):
        reply = "%s" % (self.dice.lastRollStr)
        return reply


class CustomStreamListener(tweepy.StreamListener):

    def on_status(self, status):
        
        # We'll simply print some values in a tab-delimited format
        # suitable for capturing to a flat file but you could opt 
        # store them elsewhere, retweet select statuses, etc.



        try:
            print "%s\t%s\t%s\t%s" % (status.text, 
                                      status.author.screen_name, 
                                      status.created_at, 
                                      status.source,)
            #yeah, needs refactoring
            s = {}
            s["text"] = status.text
            s["name"] = status.author.screen_name
            s["timestamp"] = status.created_at
            s["source"] = status.source
            items = db_collection.find({"counter":{"$exists": "true"}})
            l = [5]
            try:
                while True:
                    item = items.next()
                    l.append(item["counter"])
            except StopIteration:
                counter = max(l)
            s["counter"] = counter + 1
            if status.text.find(useraccount) == 0:
                rg = ReplyGenerator(status.text,item["last_roll"], counter)
                s["last_roll"] = repr(rg.dice)
                self.tweepyapi.update_status(rg.reply())
            db_collection.insert(s)

        except Exception, e:
            print >> sys.stderr, 'Encountered Exception:', e
            pass

    def on_error(self, status_code):
        print >> sys.stderr, 'Encountered error with status code:', status_code
        return True # Don't kill the stream

    def on_timeout(self):
        print >> sys.stderr, 'Timeout...'
        return True # Don't kill the stream

class TweepyHelper:
    def __init__(self,keyfile):
        f = open(keyfile)
        lines = f.readlines()
        f.close()
        consumerkey = lines[0].split("#")[0]
        consumersecret = lines[1].split("#")[0]
        accesstoken = lines[2].split("#")[0]
        accesssec = lines[3].split("#")[0]

        auth = tweepy.OAuthHandler(consumerkey, consumersecret)
        auth.set_access_token(accesstoken, accesssec)
        listener = CustomStreamListener()
        listener.tweepyapi = tweepy.API(auth)
        self.api = tweepy.streaming.Stream(auth, listener, timeout=60)

def run(api):
    api.filter(track=[useraccount])


def connect_to_db():
    global db_collection
    client = MongoClient()
    db = client.dicestream
    db_collection = db.dicecollection


if __name__ == "__main__":
    args = handle_command_line()
    connect_to_db()
    useraccount = args.username
    api = (TweepyHelper(args.keyfile)).api
    ferr = open("runningstreamingerrs.txt", "w+")
    fout = open("runningstreamingout.txt", "w+")
    if not args.test:
        with daemon.DaemonContext(pidfile=lockfile.FileLock("streamingDaemon"), files_preserve=[ferr, fout], stderr=ferr, stdout=fout):
            run(api)
    else:
       run(api)

