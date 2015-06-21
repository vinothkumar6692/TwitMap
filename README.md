# TwitMap
• Read a stream of tweets from the Twitter Live API and Amazon SQS service is used to create a processing queue for the Tweets. • Worker threads perform sentiment analysis using Alchemy API and Amazon SNS service updates the status processing on each tweet. • The HTTP frontend deployed on Amazon EC2 instance, displays a heatmap

![ScreenShot](http://s13.postimg.org/dz37p9ew7/imageedit_4_5606715183.jpg)

![ScreenShot](http://s4.postimg.org/h55x1fli5/imageedit_2_9751754738.jpg)
