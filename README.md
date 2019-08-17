Pax Britannica Enhanced
====================

Pax Britannica is a simple battleship game ported to LibGDX by [Stefan Wagner](http://bompoblog.tumblr.com/).

We have since added the following 5 new features to the original game :

### A. Design Refinement ###

Each time when a ship is being produced, a default DESIGN_BONUS factor of 1.5% will be added to
the following 3 characteristics automatically.

- Turn Speed
- Acceleration
- Hit Points

Therefore, over time, if you produce a lot of fighters, your factory ship will have more expertise
in producing fighters than your enemy who produce less number of fighters. As a result, your fighters
perform better than his.


### B. Ship Specialization ###

Each player has a special ability as follows :

```
Player 1  (Blue)  :  better fighters
Player 2   (Red)  :  better bombers
Player 3  (Green) :  better frigates
Player 4 (Yellow) :  better factory
```
In general, the speed, the acceleration and the hit point of the ship of interest are 20% better.


### C. Team Play ###

This feature allows ships to team play against one another.

You may select None (aka no team at all) OR create up to 2 teams and enroll each ship to belong to
whatever team you like.

Rule of Thumb : your aircraft will not attack your teammate(s) until after everyone in the opposite
team has died.

By default, player 1 and 2 will be in team 1 while player 3 and 4 will be in team 2.

However, if you want highest level of challenge, set up 2 teams with you on team 1 and the rest of
them on team 2 going up against you.

Before starting the game, use the number (or numeric keypad) key 1 to 4 to rotate the
team ID for each of the player (or cpu) as follows : 

```
Press key "1" to rotate team ID for Player 1
Press Key "2" to rotate team ID for Player 2
Press Key "3" to rotate team ID for Player 3
Press Key "4" to rotate team ID for Player 4
```

![teamplay](https://github.com/mokun/libgdx-demo-pax-britannica/blob/master/pax1.jpg)


### D. Two-Button Control ###

Up to a total of 4 players may engage in a hot-seat game.

Each player now is given **two** control buttons-- one for producing aircraft, one for choosing target.

The NEW Key Mappings are as follows :

```
Player 1
S : press to start the dial. hold to land on the selected quadrant. release to produce a ship
D : press to rotate target

Player 2
K : produce ship
L : rotate target

Player 3
Down : produce ship
Right : rotate target

Player 4
2 : produce ship
6 : rotate target
```

Alternatively, Player 1 (Blue) may use the left and right mouse button for control.

one may plug in more than one USB keyboards for more spacious seating.


### E. Score Board ###

We put together a score board as a 4 x 5 table.

The 4 rows shows the color of the ship (blue, red, green, and yellow) from top to bottom.

The 5 columns of stats are as follows :

- `Health`     - your ship health in %
- `# of kills` - the # and the 3 types of ships you have destroyed
- `Score`      - the composite score for each player/cpu
- `Target`     - the target ship color chosen at the moment (can be change)
- `Team ID`    - Your Team ID

![scoreboard](https://github.com/mokun/libgdx-demo-pax-britannica/blob/master/pax2.jpg)


See the original [README.md](https://github.com/libgdx/libgdx-demo-pax-britannica/blob/master/README.md) at the project site.

