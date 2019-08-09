Libgdx demo - Pax Britannica
====================

Pax Britannica is a `two-button` [RTS game by nofundsgames](http://paxbritannica.henk.ca/), which was ported to libgdx by [Stefan Wagner](http://bompoblog.tumblr.com/).

We have since added the following three features to Pax :

### A. Team Play ###

`Added 2017-11-05`

This commit introduces the the team play feature.

If you are in the same team with someone else (player or cpu), your aircraft will not attack your teammate(s).

The best would be to play 2 against 2. If you want more challenge, try playing against 3 cpu teaming together against you. 

Before starting the game, use the number (or numeric keypad) key 1 to 4 to rotate the
team ID for each of the player (or cpu) as follows : 

```
Key "1" for Player 1
Key "2" for Player 2
Key "3" for Player 3
Key "4" for Player 4
```

![teamplay](https://github.com/mokun/libgdx-demo-pax-britannica/blob/master/desktop/src/main/jetResources/pax1.png)

By default, player 1 and 2 will be in team one while player 3 and 4 will be in team 2.

The Score board is in the form of a 4 x 5 table. 

The rows are the color of the ship (blue, red, green, and yellow) from top to bottom.

The 5 columns of stats are as follows : 

- `Health`     - your ship health in %
- `# of kills` - the # and the 3 types of ships you have destroyed
- `Score`      - the composite score for each player/cpu
- `Tarage`     - the target ship color chosen at the moment (can be change)
- `Team ID`    - Your Team ID

![scoreboard](https://github.com/mokun/libgdx-demo-pax-britannica/blob/master/desktop/src/main/jetResources/pax2.png)

### B. Two-Button Control ###
`Added 2017-11-03`

Up to a total of 4 players may engage in a hot-seat game. Each player now is given **two** control buttons-- one for producing aircraft, one for choosing target.

The NEW Key Mappings are as follows :

```
Player 1
S : press/hold/release to produce aircraft
D : press to rotate target

Player 2
K : produce aircraft
L : rotate target

Player 3
Down : produce aircraft
Right : rotate target

Player 4
2 : produce aircraft
6 : rotate target
```

Alternatively, one may plug in more than one USB keyboards for more spacious seating. 

### C. Score Board ###
`Added 2017-10-27`

We put together a score board for the desktop version of the pax that will show the followings :  

- Health in %
- number of kills for the 3 types of ship are in bracket
- a composite score for each player/cpu


See the original [README.md](https://github.com/libgdx/libgdx-demo-pax-britannica/blob/master/README.md) at the project site.

