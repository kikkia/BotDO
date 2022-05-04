# War Management
You can manage attendance/signups, stats, vods and war history with the bot.

## Linking guild
To link your discord server to an in game guild user the command `linkguild <in game guild name>`
This will tie your server to that in game guild. By default it looks at NA guilds. To do other regions you just need
to set your default region to the region you play on. [Guide here](https://github.com/kikkia/BotDO/blob/master/docs/regions.md)

## How do war signups work
War signups are discord messages with a set of reactions available for people to react with.
- "Y" - you are attending the war
- "N" - you are not attending.
- "?" - you are maybe attending the war
- "refresh" - refreshes the signup message
### War Id
Each war has a unique ID, this id is on the footer of the war signup and is used for interacting with the war in many ways.


## Making a war signup
To make a war signup you have 2 choises. Making "one-off" wars. These wars on non-recurring. To do so use the `,addwar` 
command with the date the war is on.  
Example:  
`,addwar 25-06-2021` - Adds a war on the 25th of June, 2021 in its own channel.

The other way is to setup war days. War days are the days you war on every week.  
Using the `,setwardays` command will make 1 channel for each day and wars for that day of the week will be in that channel.  
Example
`,setwardays sunday,monday,friday` - sets the guild to have recurring wars on sunday, monday and friday.  
One channel will be made for each day and the war signups for the days will show up in their respective channel.  

### Adding a node
If you want to add what node you are fighting on you can use the `,node` command. This will pull down node specific things to the signup.  
Things like member caps, Tier, Gear caps, etc will be posted and used for signups.  
Example
`,node 7::wolf hills` - Sets the war with id 7 to be on wolf hills. Can be done before or after archiving.


## Once war is done
### Setup a war signup archive channel
When wars are finished, you need to specify a channel that all archived war signups go in.  
You can do this with the `,setarchive` command to set a channel to recieve archived war signups.
Example  
`,setarchive #archive-channel` - Sets it up so that archived wars get posted in the channel #archive-channel.  

### Archive a war
Once the war is finished just post `,archive` in the channel with the signup message. If the war is recurring (on a specified war day)
it will make next weeks signup and archive the old signup, if not it will just archive the war signup.  

### Add stats or vods to archived wars
If you want to keep track of war stats and/or vods, you can add them with the following commands:  
- `,addvod id::name of vod::url to vod` - Adds a vod to a war with a given id. Links to youtube or twitch vods work best.
- `,addstats id (attatch screenshot of stats to message)` - Will add the stats screenshot linked to war with given id.
