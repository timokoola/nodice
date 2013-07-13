import tweepy, sys, os
import argparse # requires 2.7
import random, re


diere = re.compile('(\d+)[dD](\d+)([+-]?\d*)')

def handle_command_line():
    parser = argparse.ArgumentParser(description="Run streaming twitter bot")
    parser.add_argument("-t", "--test", help="Get a test response")
    parser.add_argument("-k", "--keyfile", help="Twitter account consumer and accesstokens")
    parser.add_argument("-u", "--username", help="Username to follow")
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
        self.lastRollStr = "Rolled %d for rolling %s = (%s%s)" % (self.lastRoll, repr(self), "+".join(map(str,val)), self.mod_str())


class DiceParser:
    def __init__(self,description):
        mth = diere.match(description)
        if mth:
            self.die = Dice(mth.groups())
        else:
            self.die = Dice([1,6,0])

class ReplyGenerator:
    def __init__(self,tweet,replyto):
        self.tweet = tweet
        self.replyto = replyto
        dp = DiceParser(tweet)
        self.dice = dp.die
        self.dice.roll()

    def reply(self):
        reply = "@%s %s" % (self.replyto,self.dice.lastRollStr)
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
            if status.text.find("@timotestikoola") == 0:
                rg = ReplyGenerator(status.text,status.author.screen_name)
                self.tweepyapi.update_status(rg.reply())

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



if __name__ == "__main__":
    args = handle_command_line()
    if not args.test:
        api = (TweepyHelper(args.keyfile)).api
        api.filter(track=["@timotestikoola"])
    else:
        r = ReplyGenerator(args.test, "tester")
        print r.reply()

