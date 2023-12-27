cooking-telegram-bot
===

* [Description](#description)
* [Useful links](#useful-links)
* [How to run](#how-to-run)
    * [Pre-requests](#pre-requests)
    * [Locally by maven](#locally-by-maven)
    * [Locally by docker](#locally-by-docker)
    * [GCP](#gcp)

## Description

If you don't know what you want to cook (like I usually do)\
Just open the bot, click "Start сooking" and just follow to notifications.

## Useful links

[Telegram Bot](tbd)\
[Concept board](https://miro.com/app/board/uXjVNHdZ5Oc=/?share_link_id=42246636944)

## How to run

### Pre-requests

* [create](https://core.telegram.org/bots/features#creating-a-new-bot) the telegram bot;
* set COOKING_BOT_TOKEN and COOKING_BOT_NAME to environment variables;
* obtain own public and secured https url and set it to WEBHOOK_URL also (eg ngrok http 8083);

### Locally by maven

```shell
mvnw sping-boot:run
```

### Locally by docker

tbd

### GCP

tbd