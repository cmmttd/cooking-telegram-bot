# cooking-telegram-bot
[Telegram Bot](tbd)
[Concept board](https://miro.com/app/board/uXjVNHdZ5Oc=/?share_link_id=42246636944)

If you don't know what you want to cook (like I usually do)
Just click "Start сooking" and the bot will tell you when it's time for the next step.

##How to run locally
Pre-requests:
* [create](https://core.telegram.org/bots/features#creating-a-new-bot) the telegram bot;
* set COOKING_BOT_TOKEN and COOKING_BOT_NAME to environment variables; 
* obtain own public and secured https url and set it to WEBHOOK_URL also;

```shell
$ mvn package
$ java -jar /target/*.jar
```

##How to run locally by docker
tbd

##How to run in GCP
tbd