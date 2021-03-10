# Scroll Tracking
You can use this feature to track your own scroll inventory as well as setup groups so you can see the boss scroll inventory
of yourself and others in the group.


## Adding scrolls to your inventory
There are 2 ways to add scrolls to your inventory. Either using the `,addscrolls` command (Adds given scrolls to your current inventory) 
or the `,updatescrolls` command (updates your inventory to exactly the given scrolls)

### Command arguments
The format to give when adding/updating/completing your inventory is `<command> (#) (scroll), (#) (scroll), ...`  
Example: To add 3 bheg and 1 ronin scroll to your inventory this is what the command looks like
> ,addscroll 3 bheg, 1 ronin

### All accepted scroll names can be found [here](scroll_names.md)

## Getting a users scroll inventory
To get the inventory of a user, use the `,scrolls` command. Just use the command to get your own, or else use `,scrolls user_id` to get the inventory of another user.

## Completing scrolls
When you complete boss scrolls in game you can use the `,doscrolls <scrolls args>` to mark those scrolls as completed. They will then be removed 
from your inventory and an entry in your scroll history.

## Getting a users history
You can obtain a record of when you completed scrolls and what scrolls were completed by using the `,scrollhistory` command.

## Scroll Groups
You create scroll groups to organize users and see all of their scroll inventories at once.  
You can create a group with the `,addscrollgroup name` command.   

### Managing users
To join a scroll group a user needs to use the `,joinscrollgroup groupname` command. Similarly, you can leave a scroll group 
with the `,leavescrollgroup groupname` command.  
Admins can also remove users from scroll groups using the `,removeuser groupname family_name_or_discord_name`

### Getting group info
You can get the info of a group with the command `,groupinfo name` command. This will display a table of all users scrolls.  
