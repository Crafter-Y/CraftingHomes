# CraftingHomes

A simple Home plugin.

By itself, this is supposed to be a simple Home 
plugin (for now) that has the basic commands `/home`, `/homes`,
`/sethome`, `/delhome` and 3 Homes per player.

## Configuration

Several things can be configured in the `config.yml` file.

- maxHomes: The maximum amount of homes a player can have, default: 3
- language: The language to use, default: en

You can add your own language by creating a new file your_language.lang.yml

## Roadmap
- [x] Persistent Data Storage
- [x] Configurable Homes per player
- [x] reflection for command discovery
- [x] abstract command definition syntax (with annotations)
- [x] configurable i18n
- [ ] other implementations like fabric or forge
