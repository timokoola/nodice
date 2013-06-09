import tweepy, sys, os
import argparse # requires 2.7
import random


def handle_command_line():
    parser = argparse.ArgumentParser(description="Run streaming twitter bot")
    parser.add_argument("-t", "--test", help="Get a test response",
            type=string,default="hello" )
    parser.add_argument("-k", "--keyfile", help="Twitter account consumer and accesstokens")
    args = parser.parse_args()
    return args


class ReplyGenerator:
	def __init__(self,tweet):
		self.tweet = tweet

	def reply(self):
		r = random.Random()
		reply = "@%s %d" % (self.tweet.author.screen_name,r.randint(1,6))
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
            if status.text.find("@dicenessapp") == 0:
            	rg = ReplyGenerator(status)
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

    api = (TweepyHelper(args.keyfile)).api
    api.filter(track=["@dicenessapp"])


