Pax Britannica Enhanced
====================

Pax Britannica is a simple battle ship game ported to LibGDX by [Stefan Wagner](http://bompoblog.tumblr.com/).

Up to a total of 4 players may engage in a hot-seat game.

Each player now is given **three** control buttons-- one for shielding up, one producing aircraft, one choosing target.

New Keyboard Mapping :

Player 1
| Key | Purpose |
|---|---|
|<kbd>a</kbd> |  Press to start the dial around the center of the factory ship. Hold and wait until it reaches the selected quadrant. Release to produce a ship. |
|<kbd>s</kbd> |  Press and hold to create an invisible shield around the factory ship. Release to stop. |
|<kbd>d</kbd> |  Press to rotate to a different target |

Player 2
| Key | Purpose |
|---|---|
|<kbd>j</kbd> | Produce ship |
|<kbd>k</kbd> | Shield up |
|<kbd>l</kbd> | Rotate target |

Player 3
| Key | Purpose |
|---|---|
|<kbd>left</kbd>  | Produce ship |
|<kbd>down</kbd>  | Shield up |
|<kbd>right</kbd> | Rotate target |

Player 4
| Key | Purpose |
|---|---|
|<kbd>4</kbd>(num pad) | Produce ship |
|<kbd>2</kbd>(num pad) | Shield up |
|<kbd>6</kbd>(num pad) | Rotate target |


Alternatively, Player 1 (Blue) may use the mouse's left, middle and right mouse button for control.

Player 1
| Mouse Button | Purpose |
|---|---|
|`Left`| Produce ship |
|`Middle`| Shield up |
|`Right`| Rotate target |


one may plug in more than one USB keyboards for more spacious seating.

We have since added the following 6 new features to the original game :

- A. Shield's Up
- B. Design Refinement
- C. Ship Specialization
- D. Team Play
- E. Targeting An Opponent
- F. Score Board



```
NOTE: for a hotseat game with 4 human players, make sure your keyboard num lock is ON so that Player 4 may use it. If the num lock is OFF, the
<kbd>2</kbd> and <kbd>6</kbd> keys would be mapped to <kbd>down</kbd> and <kbd>right</kbd> instead.
```

### A. Shield's Up ###

Enable player's factory ship to shield up. This lowers the damage done to the hit points of the
factory ship up to 50% of what it would have caused.

| Player/CPU | Color | Shield Up |
|---|---|---|
| 1 |  `Blue`  | <kbd>s</kbd> |
| 2 |  `Red`   | <kbd>k</kbd> |
| 3 | `Green`  | <kbd>down</kbd> |
| 4 | `Yellow` | <kbd>2</kbd>(numpad) |

Note: When activating the shield, it will temporarily drain the pool of resources being collected.
Therefore, the resource normally used for constructing ships and upgrading the factory ship may be
used to raise the shield to mitigate damages from attack.


### B. Design Refinement ###

Each time when a ship is being produced, a default DESIGN_BONUS factor of 1.5% will be added to
the following 3 characteristics automatically.

- Turn Speed
- Acceleration
- Hit Points

Therefore, over time, if you produce a lot of fighters, your factory ship will have more expertise
in producing fighters than your enemy who produce less number of fighters. As a result, your fighters
perform better than his.


### C. Ship Specialization ###

Each player has a special ability as follows :

| Player/CPU | Color | Purpose |
|---|---|---|
| 1 |  `Blue`  | better fighters |
| 2 |  `Red`   | better bombers  |
| 3 | `Green`  | better frigates |
| 4 | `Yellow` | better factory |

In general, the speed, the acceleration and the hit point of the ship of interest are 20% better.


### D. Team Play ###

This feature allows ships to team play against one another.

You may select None (aka no team at all) OR create up to 2 teams and enroll each ship to belong to
whatever team you like.

Rule of Thumb : your aircraft will not attack your teammate(s) until after everyone in the opposite
team has died.

By default, player 1 and 2 will be in team 1 while player 3 and 4 will be in team 2.

However, if you want highest level of challenge, set up 2 teams with you on team 1 and the rest of
them on team 2 going up against you.

In the `Main Menu`, use the number (or numeric keypad) key 1 to 4 to rotate the
team ID for each of the player (or cpu) as follows : 

| Key | Purpose |
|---|---|
| <kbd>1</kbd> | rotate the team ID for Player/CPU 1 |
| <kbd>2</kbd> | rotate the team ID for Player/CPU 2 |
| <kbd>3</kbd> | rotate the team ID for Player/CPU 3 |
| <kbd>4</kbd> | rotate the team ID for Player/CPU 4 |


![teamplay](https://github.com/mokun/libgdx-demo-pax-britannica/blob/master/pax1.jpg)

Also, use the following keys to choose between Player and CPU in the `Main Menu`:

| Key | Purpose |
|---|---|
| <kbd>s</kbd> | rotate between Player 1 and CPU 1 for the `Blue` ship |
| <kbd>k</kbd> | rotate between Player 2 and CPU 2 for the `Red` ship |
| <kbd>down</kbd> | rotate between Player 3 and CPU 3 for the `Green` ship |
| <kbd>2</kbd>(numpad) | rotate between Player 4 and CPU 4 for the `Yellow` ship |


Note: if there are only two remaining factory ships and you are one of them, we call this the final epic battle.
You will compete with even your teammate for the final trophy.


### E. Targeting An Opponent ###

Player may choose to focus his attack on a particular opponent instead of spreading it over everyone.

All it takes is to highlight the color of the target Player/CPU.

The `Target` column on the Score Table shows which target for each Player/CPU.


### F. Score Board ###

We put together a score board as a 4 x 5 table.

The 4 rows shows the color of the ship (blue, red, green, and yellow) from top to bottom.

The 5 columns of stats are as follows :

| Key | Purpose |
|---|---|
| `Health` | Your ship's health in % |
| `# of kills` | The 3 columns are the types of ships (Fighter, Bomber, Frigate) and the # shows how many ships of that type you have destroyed |
| `Score` | Your composite score |
| `Target` | The target ship color you have chosen at the moment (can be change) |
| `Team ID` | Your Team ID |

![scoreboard](https://github.com/mokun/libgdx-demo-pax-britannica/blob/master/pax2.jpg)

See the original [README.md](https://github.com/libgdx/libgdx-demo-pax-britannica/blob/master/README.md) at the project site.

