# Damage Handler

###### A Minecraft Plugin that modifies Minecraft combat mechanics by handle all types of damage and adding elemental systems and elemental reaction systems into the game's combat system.

## Commands

- `/damagehandle reload` reload plugin configuration

## MythicMobs Mechanics


### elemental_damage
Deal elemental damage or physical damage


| Attribute  | Aliases | Data Type | Description           | Default   |
|------------|---------|-----------|-----------------------|-----------|
| amount     | a       | Double    | Amount of damage      | 0         | 
| element    | e       | String    | Element of the damage | (default) | 
| gauge_unit | gu      | String    | Aura gauge unit       | (default) |

Examples:
```yml
test:
  Skills:
  - elemental_damage{amount=10;element=PYRO;gu=1.0A} @Target
```

### elemental_shield
Create elemental shields according to the specified quantity

| Attribute | Aliases | Data Type | Description                               | Default |
|-----------|---------|-----------|-------------------------------------------|---------|
| amount    | a       | Double    | Quantity of the elemental shield          | 0       | 
| element   | e       | String    | Element of the shield                     |         | 
| duration  | d, t    | Long      | Duration of the elemental shield in ticks | 0       |

Examples:
```yml
test:
  Skills:
  - elemental_shield{amount=10;element=GEO;duration=100} @Target # apply geo shield for 5 seconds
```

### reduce_defense
Reduce defensive power according to the specified quantity and duration

| Attribute | Aliases | Data Type | Description                             | Default  |
|-----------|---------|-----------|-----------------------------------------|----------|
| amount    | a       | Double    | The amount of defense to be reduced (%) | 0        | 
| duration  | d, t    | Long      | The duration for the reduction          | 0        |

Examples:
```yml
test:
  Skills:
  - reduce_defense{amount=10;duration=100} @Target # reduce 10 defense for 5 seconds
```

### reduce_resistance
Reduce the resistance of the specified element

| Attribute | Aliases | Data Type | Description                                | Default |
|-----------|---------|-----------|--------------------------------------------|---------|
| amount    | a       | Double    | The amount of resistance to be reduced (%) | 0       | 
| element   | e       | String    | The element of resistance                  |         |
| duration  | d, t    | Long      | The duration for the reduction             | 0       |

Examples:
```yml
test:
  Skills:
  - reduce_resistance{amount=50;element=ANEMO;duration=100} @Target # reduce 50% of anemo resistance
```

## Installations

