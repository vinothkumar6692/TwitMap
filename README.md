# TwitMap
• Read a stream of tweets from the Twitter Live API and Amazon SQS service is used to create a processing queue for the Tweets. • Worker threads perform sentiment analysis using Alchemy API and Amazon SNS service updates the status processing on each tweet. • The HTTP frontend deployed on Amazon EC2 instance, displays a heatmap
