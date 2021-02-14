# BotDO
A BDO Centric discord bot

Currently under development

## Features
- User Scroll inventory (Complete)
- Scroll groups (Pretty much done)
- Events (In progress)
- Family name -> discord sync (In progress)
- Boss notifications (planned)
- Member Time zone tracking (planned)
- Table of contents (planned)
- Stats tracking (planned)

Feel free to contribute or suggest more features. Docs are basically non-existant atm but I am working on them. 


#### Running locally
1. You will need to have a mysql db to run the bot against, this will come prepacked in a docker-compose file later, but for now you need one.
2. You will need a discord bot user registered on https://discord.com/developers/applications, you will use this test bot to run the code through.
3. Create a file at `src/resources/application.yml` for housing configs
4. Fill in this template for the file:
```yaml
discord:
  token: "discord-bot-token"
  owner: "discord-id-of-owner"

spring:
  datasource:
    url: "jdbc:mysql://db-uri:port/schema-name"
    username: "db-username"
    password: "db-password"
  jpa:
    properties:
      hibernate:
        dialect: "org.hibernate.dialect.MySQL5InnoDBDialect"
    hibernate:
      ddl-auto: "validate"
```
4. It should now run and boot the spring application.
