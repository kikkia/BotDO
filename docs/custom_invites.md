# Custom Invites

## Default values  
You can set the following default values on your server:  
- Entry Channel: The default channel to make invites for.
- Welcome Channel: The default channel to send welcome messages.
- Recruit Role: The default role to assign new recruits.
- Recruit welcome message: The default welcome messages for recruit invites. 

### Recruit invites
You can quickly make recruit invites that are defaulted to 1 use and will use the recruit defaults from above if set.
Example use: ```,rinvite```

### Custom invites
You can make more custom invites that accept different arguments to change behavior.
These arguments are 
- ```-u```: Uses: The number of uses the invite can be used (DEFAULT: 1)
- ```-c```: Channel: to make the invite for (DEFAULT: entry channel)
- ```-g```: Guild name: this will put a prefix in front of their name when they join. ```-g test``` will add ```(test)``` in front of their name. 
- ```-r```: Roles: Role names of roles they will get when joining with the invite. (comma separated for list)
- ```-w```: Welcome message: A message to post to the default welcome channel when they join. ```%name%``` translates to the joining persons name.

Some examples: 
- ```,cinvite -u 10 -g test -r foreign, friend```: Creates an invite with 10 max uses, adds guild prefix ```(test)``` and will add the ```foreign and friend``` roles to them.