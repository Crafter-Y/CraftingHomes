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

## Technical insight

I know that i over-complicated many things, but this is a kind of exercise for me.
You may look at the `craftinghomes` subproject that contains the whole logic. 
Everything else is just to make it work.

## Roadmap
- [x] Persistent Data Storage
- [x] Configurable Homes per player
- [x] reflection for command discovery
- [x] abstract command definition syntax (with annotations)
- [x] configurable i18n
- [x] reflection to discover and set command arguments
- [ ] gui for /homes
- [x] /homes <player> for admins with permission
- [ ] /homes <player> <home> for admins with permission
- [ ] data storage templating
- [ ] other implementations like fabric or forge (once feature complete)
- [ ] more ci tests
- [ ] unit tests
