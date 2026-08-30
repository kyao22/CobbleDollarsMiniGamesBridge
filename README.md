# CobbleDollars MiniGames Bridge

CobbleDollars MiniGames Bridge is a Fabric compatibility mod that connects the CobbleDollars economy with the Cobblemon MiniGames arcade economy. Instead of keeping these two systems separate, it allows mini-game coin balance and CobbleDollars balance to work together in a unified way.

## Overview

This mod is built for servers and players who want a single economy flow across both:

- CobbleDollars
- Cobblemon MiniGames rewards and spending

The core idea is simple: the mini-game economy still behaves like its own arcade system, but the value is tied back to the CobbleDollars balance. This creates a smoother progression system where rewards, payouts, and spending feel consistent across the server economy.

## Benefits

- Unifies mini-game rewards with the CobbleDollars economy
- Reduces the feeling of having disconnected currency systems
- Makes mini-game progression feel more meaningful for players
- Keeps the existing mini-game economy structure intact while adding compatibility with CobbleDollars
- Provides a lightweight bridge instead of requiring a full economy rewrite

## Main purpose

The mod exists to solve a common problem in custom Minecraft server setups: different gameplay systems often use different money sources. CobbleDollars is a player economy system, while Cobblemon MiniGames has its own arcade coin system. Without a bridge, players may earn rewards in one system and spend in another with no clear connection.

This project closes that gap by translating between the two systems in a controlled way.

## Mod compatibility

This project targets the following ecosystem:

- Fabric
- Minecraft 1.21.1
- CobbleDollars
- Cobblemon
- Cobblemon MiniGames

## Supported versions

- Minecraft: 1.21.1
- Mod loader: Fabric
- CobbleDollars: 2.0.0 Beta-5.1+
- Cobblemon: 1.7.0+
- Cobblemon MiniGames: 3.0.2
- Fabric API: 0.116.6+ for 1.21.1

## Trade ratio

The bridge converts CobbleDollars into mini-game arcade coins using a fixed exchange rate:

- 1 CobbleDollars = 50 mini-game arcade coins

This means the economy can treat 50 arcade coins as the equivalent value of 1 CobbleDollars unit for reward conversion, spending checks, and balance bridging.

## How the bridge works

The mod hooks into the mini-game economy class and overrides the methods responsible for:

- counting arcade coins
- spending arcade coins
- adding arcade coins to a player

From there, it reads the player’s CobbleDollars balance and converts it into mini-game coin equivalents using an internal conversion rate. The current implementation uses a direct conversion where CobbleDollars value can be expressed as mini-game arcade coin value for the economy checks.

In practical terms:

- when the player has CobbleDollars, those dollars are treated as equivalent mini-game currency value
- when mini-game rewards are granted, the value is added to the CobbleDollars balance
- when the player spends in mini-games, the system checks whether the equivalent value is available and deducts it accordingly

This approach keeps the economy logic familiar to the game while allowing CobbleDollars to act as the underlying economic layer.

## Design philosophy

This mod is not a full replacement of the mini-game economy. Instead, it is a compatibility layer that preserves the original game flow while integrating the player’s CobbleDollars balance into it. That keeps the experience stable while making the economy more consistent from a server-management perspective.

## Use case

This mod is especially useful for servers that want:

- one economy to feel consistent across gameplay systems
- mini-game rewards to matter beyond the arcade itself
- a cleaner player progression loop between CobbleDollars and mini-game content

## Summary

CobbleDollars MiniGames Bridge is a lightweight economy bridge for Fabric servers running CobbleDollars and Cobblemon MiniGames together. It connects the two currency systems so the arcade economy can interact with CobbleDollars in a seamless way, making the server economy feel more unified and intentional for players.
