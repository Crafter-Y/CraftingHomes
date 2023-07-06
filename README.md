# CraftingHomes

-----------------
A simple Home plugin.

By itself, this is supposed to be a simple Home 
plugin (for now) that has the basic commands `/home`, `/homes`,
`/sethome`, `/delhome` and 3 Homes per player.

## Configuration

Several things can be configured in the `config.yml` file.

- maxHomes: The maximum amount of homes a player can have, default: 3

## Roadmap
- [x] Persistent Data Storage
- [x] Configurable Homes per player
- [ ] Translation
- [ ] Dependency Injection for command discovery
- [ ] abstract command definition syntax (with annotations)
- [ ] possibility for other implementations like fabric or forge