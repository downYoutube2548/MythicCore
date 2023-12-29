# Mythic Core

A Minecraft Plugin that modifies Minecraft combat mechanics by handle all types of damage and adding elemental systems and elemental reaction systems into the game's combat system.

## Commands

- `/mythiccore reload` reload plugin configuration

## MythicMobs Mechanics

### snapshot
Execute a Mythic Mob skills with Snapshot stats (Player Only)


| Attribute | Aliases   | Data Type | Description          | Default |
|-----------|-----------|-----------|----------------------|---------|
| skill     | s, spell  | String    | Mythic Mob Skill ID  |         |

Examples:
```yml
test:
  Skills:
  - snapshot{s=SKILL_ID}
```

### elemental_damage
Deal elemental damage or physical damage


| Attribute         | Aliases | Data Type    | Description                                 | Default   |
|-------------------|---------|--------------|---------------------------------------------|-----------|
| amount            | a       | Double       | จำนวนดาเมจ                                  | 0         | 
| element           | e       | String       | ประเภทของธาตุ                               | (default) | 
| gauge_unit        | gu      | String       | เกจของออร่าธาตุที่จะติด (Ex. 1A, 2.2B, 4C)  | (default) |
| internal_cooldown | icd     | String, Long | คูลดาวน์การติดธาตุและการเกิดปฏิกิริยาธาตุ   | default   | 
| formula           | dc, f   | String       | สูตรคำนวนดาเมจตาม config.yml ของ MythicCore | (default) |
| percent           | p       | Double       | Talent (%)                                  | 100       |

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

### set_elemental_damage
Set mythic mob damage

| Attribute         | Aliases | Data Type    | Description                                 | Default   |
|-------------------|---------|--------------|---------------------------------------------|-----------|
| amount            | a       | Double       | จำนวนดาเมจ                                  | 0         | 
| element           | e       | String       | ประเภทของธาตุ                               | (default) | 
| gauge_unit        | gu      | String       | เกจของออร่าธาตุที่จะติด (Ex. 1A, 2.2B, 4C)  | (default) |
| internal_cooldown | icd     | String, Long | คูลดาวน์การติดธาตุและการเกิดปฏิกิริยาธาตุ   | default   | 
| formula           | dc, f   | String       | สูตรคำนวนดาเมจตาม config.yml ของ MythicCore | (default) |
| percent           | p       | Double       | Talent (%)                                  | 100       |

Examples:
```yml
test:
  Skills:
    - set_elemental_damage{a=10;e=PYRO;icd=20} @self ~onSpawn
    - set_elemental_damage{a=20;e=PYRO;gu=4B;icd=test} @self ~onSpawn
```

### set_defense
Set mythic mob defense

| Attribute         | Aliases | Data Type      | Description       | Default   |
|-------------------|---------|----------------|-------------------|-----------|
| amount            | a       | Double         | Amount of defense | 0         | 

Examples:
```yml
test:
  Skills:
    - set_defense{a=300} @self ~onSpawn
```

### set_resistance
Set mythic mob elemental resistance

| Attribute | Aliases | Data Type | Description          | Default   |
|-----------|---------|-----------|----------------------|-----------|
| amount    | a       | Double    | Amount of defense    | 0         | 
| element   | e       | String    | Resistance element   | (default) |

Examples:
```yml
test:
  Skills:
    - set_resistance{a=20;e=CRYO} @self ~onSpawn
```

## Installations
1. Register elements to MythicLibs plugin `plugins/MythicLibs/elements.yml`
   - PHYSICAL
   - GEO
   - ELECTRO
   - DENDRO
   - HYDRO
   - PYRO
   - CRYO

2. Add this to `plugins/MMOItems/language/stats.yml`
   ```yaml
      ast-gauge-unit: '&3 &7เกจออร่าของธาตุ: &f{value}'
      ast-internal-cooldown: '&3 &7คูลดาวน์การติดออร่าธาตุ: &f{value}'
   
      ast-critical-rate: '&3 &7อัตราคริติคอล: &f{value}%'
      ast-critical-damage: '&3 &7ความแรงคริติคอล: &f{value}%'
   
      ast-ignore-defense: '&r &7มองข้ามพลังป้องกัน: &f{value}%'
      ast-attack-damage-buff: '&r &7พลังโจมตี: &f+{value}'
      ast-attack-damage-buff-percent: '&r &7พลังโจมตี: &f+{value}%'
   
      ast-defense-buff: '&r &7พลังป้องกัน: &f+{value}'
      ast-defense-buff-percent: '&r &7พลังป้องกัน: &f+{value}%'
   
      ast-elemental-mastery: '&r &7ความชำนาญธาตุ: &f{value}'
   
      ast-elemental-percent: '&r &7สร้างความเสียหาย{color}{element}&7: &f{value}%'
      ast-elemental-damage-bonus: '&r &7โบนัสความเสียหาย{color}{element}&7: &f+{value}%'
      ast-elemental-resistance: '&r &7ความต้านทาน{color}{element}&7: &f+{value}%'
   
      ast-all-elemental-resistance: '&r &7เพิ่มความต้านทานทุกธาตุ: &f+{value}%'
      ast-all-elemental-damage-bonus: '&r &7เพิ่มโบนัสความเสียหายทุกธาตุ: &f+{value}%'
   ```
   
3. Using stats id above to specify lore format of MMOItem in `plugins/MMOItems/language/lore-format.yml`
4. Disable damage indicators from MythicLibs plugin.