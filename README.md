# Mythic Core

A Minecraft Plugin that modifies Minecraft combat mechanics by handle all types of damage and adding elemental systems and elemental reaction systems into the game's combat system.

## Commands

- `/mythiccore reload` reload plugin configuration

## MythicMobs Mechanics

### snapshot
Execute a Mythic Mob skills with Snapshot stats (Player Only)<br /><br />

| Attribute   | Aliases  | Data Type | Description                                           | Default |
|-------------|----------|-----------|-------------------------------------------------------|---------|
| skill       | s, spell | String    | Mythic Mob Skill ID                                   |         |
| check_owner | co       | Boolean   | Use caster owner's snapshot stats if owner is present | false   |
| ANY_KEY     |          | Double    | Specify the key-value of current Snapshot Skill       | -       |
<br />

#### Additional Targeters

| Targeter  | Description                                    | 
|-----------|------------------------------------------------|
| @snapshot | Use the targeter of the current Snapshot Skill |
<br />

#### Additional Placeholders
| Snapshot Placeholder  | Function                                      | 
|-----------------------|-----------------------------------------------|
| <snapshot.damage>     | Return Attacker's Snapshot base attack damage |
| <snapshot.def>        | Return Attacker's Snapshot base defense       |
| <snapshot.hp>         | Return Attacker's Snapshot max HP             |
| <snapshot.em>         | Return Attacker's Snapshot elemental mastery  |
| <snapshot.stat.STAT>  | Return Attacker's Specific snapshot stat      |
| <snapshot.ANY_KEY>    | Return the value of specific key              |
<br />

Examples:
```yml
test:
  Skills:
  - snapshot{s=test2} @target
  - snapshot{s=test3;some_damage=<caster.damage>} @EIR{r=5}

test2:
  Skills:
  - elemental_damage{a=<snapshot.damage>;element=ELECTRO} @snapshot
  
test3:
  Skills:
  - elemental_damage{a=<snapshot.some_damage>;element=CRYO} @snapshot # deal <caster.damage> Damage to Entities in radius 5
```

<br />

### elemental_damage
Deal elemental damage or physical damage <br /><br />


| Attribute         | Aliases | Data Type    | Description                                 | Default   |
|-------------------|---------|--------------|---------------------------------------------|-----------|
| amount            | a       | Double       | จำนวนดาเมจ                                  | 0         | 
| element           | e       | String       | ประเภทของธาตุ                               | (default) | 
| gauge_unit        | gu      | String       | เกจของออร่าธาตุที่จะติด (Ex. 1A, 2.2B, 4C)  | (default) |
| internal_cooldown | icd     | String, Long | คูลดาวน์การติดธาตุและการเกิดปฏิกิริยาธาตุ   | default   | 
| formula           | dc, f   | String       | สูตรคำนวนดาเมจตาม config.yml ของ MythicCore | (default) |
| percent           | p       | Double       | Talent (%)                                  | 100       |

<br />

Examples:
```yml
test:
  Skills:
  - elemental_damage{amount=10;element=PYRO;gu=1.0A} @Target
```
<br />

### elemental_shield
Create elemental shields according to the specified quantity<br /><br />

| Attribute | Aliases | Data Type | Description                               | Default |
|-----------|---------|-----------|-------------------------------------------|---------|
| amount    | a       | Double    | Quantity of the elemental shield          | 0       | 
| element   | e       | String    | Element of the shield                     |         | 
| duration  | d, t    | Long      | Duration of the elemental shield in ticks | 0       |

<br />

Examples:
```yml
test:
  Skills:
  - elemental_shield{amount=10;element=GEO;duration=100} @Target # apply geo shield for 5 seconds
```
<br />

### reduce_defense
Reduce defensive power according to the specified quantity and duration<br /><br />

| Attribute | Aliases | Data Type | Description                             | Default  |
|-----------|---------|-----------|-----------------------------------------|----------|
| amount    | a       | Double    | The amount of defense to be reduced (%) | 0        | 
| duration  | d, t    | Long      | The duration for the reduction          | 0        |

<br />

Examples:
```yml
test:
  Skills:
  - reduce_defense{amount=10;duration=100} @Target # reduce 10 defense for 5 seconds
```
<br />

### reduce_resistance
Reduce the resistance of the specified element<br /><br />

| Attribute | Aliases | Data Type | Description                                | Default |
|-----------|---------|-----------|--------------------------------------------|---------|
| amount    | a       | Double    | The amount of resistance to be reduced (%) | 0       | 
| element   | e       | String    | The element of resistance                  |         |
| duration  | d, t    | Long      | The duration for the reduction             | 0       |

<br />

Examples:
```yml
test:
  Skills:
  - reduce_resistance{amount=50;element=ANEMO;duration=100} @Target # reduce 50% of anemo resistance
```
<br />

### set_elemental_damage
Set mythic mob damage<br /><br />

| Attribute         | Aliases | Data Type    | Description                                 | Default   |
|-------------------|---------|--------------|---------------------------------------------|-----------|
| amount            | a       | Double       | จำนวนดาเมจ                                  | 0         | 
| element           | e       | String       | ประเภทของธาตุ                               | (default) | 
| gauge_unit        | gu      | String       | เกจของออร่าธาตุที่จะติด (Ex. 1A, 2.2B, 4C)  | (default) |
| internal_cooldown | icd     | String, Long | คูลดาวน์การติดธาตุและการเกิดปฏิกิริยาธาตุ   | default   | 
| formula           | dc, f   | String       | สูตรคำนวนดาเมจตาม config.yml ของ MythicCore | (default) |
| percent           | p       | Double       | Talent (%)                                  | 100       |

<br />

Examples:
```yml
test:
  Skills:
    - set_elemental_damage{a=10;e=PYRO;icd=20} @self ~onSpawn
    - set_elemental_damage{a=20;e=PYRO;gu=4B;icd=test} @self ~onSpawn
```
<br />

### set_defense
Set mythic mob defense<br /><br />

| Attribute         | Aliases | Data Type      | Description       | Default   |
|-------------------|---------|----------------|-------------------|-----------|
| amount            | a       | Double         | Amount of defense | 0         | 

<br />

Examples:
```yml
test:
  Skills:
    - set_defense{a=300} @self ~onSpawn
```
<br />

### set_resistance
Set mythic mob elemental resistance<br /><br />

| Attribute | Aliases | Data Type | Description          | Default   |
|-----------|---------|-----------|----------------------|-----------|
| amount    | a       | Double    | Amount of defense    | 0         | 
| element   | e       | String    | Resistance element   | (default) |

<br />

Examples:
```yml
test:
  Skills:
    - set_resistance{a=20;e=CRYO} @self ~onSpawn
```
<br />

### clearAura
Remove or clear target's aura <br /><br />


| Attribute | Aliases | Data Type | Description    | Default |
|-----------|---------|-----------|----------------|---------|
| aura      | a       | String    | Aura to remove | ALL     | 

<br />

Examples:
```yml
test:
  Skills:
  - clearAura{aura=PYRO} @target
  - clearAura{aura=FROZEN} @target
  - clearAura{aura=ALL} @target
  - clearAura{} @target
```
<br />

### applyAura
Apply aura to target <br /><br />


| Attribute        | Aliases | Data Type | Description                 | Default   |
|------------------|---------|-----------|-----------------------------|-----------|
| aura             | a       | String    | Aura to remove              |           | 
| gauge_unit       | gu      | String    | Elemental Gauge             | (default) |
| trigger_reaction | tr      | Boolean   | Trigger elemental reactions | true      |

<br />

Examples:
```yml
test:
  Skills:
  - applyAura{aura=PYRO;gu=4C;tr=true} @target
```
<br />

## Installations
<br />

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