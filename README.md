#[Tinkers' Steelworks](http://www.minecraftforum.net/topic/2227330-)

A steel-based expansion for Tinkers' Construct.

##Issue reporting
Please include the following:

* Minecraft version
* Forge version/build
* Tinkers' Construct version
* Tinkers' Steelworks version
* Versions of any mods potentially related to the issue 
* Any relevant screenshots are greatly appreciated.
* For crashes:
    * Steps to reproduce
    * ForgeModLoader-client-0.log (the FML log) from the root folder of the client

## Project Style
Indentation is 1 tab character. Braces on same line. Braces may be omitted for single-statement conditions and loops, with the statement indented on the next line. [Sun/Oracle guidelines](http://www.oracle.com/technetwork/java/javase/documentation/codeconvtoc-136057.html) for everything else.

## Development
Launch setupCookieCore.sh the setup your workspace with gradle.

##Licenses
Most code is licensed under [Creative Commons 3.0](http://creativecommons.org/licenses/by/3.0/).

##Modpacks
You're free to include this mod in any modpack

##Minetweaker support

TSteelworks comes with built-in minetweaker3 support, here is a list of available methods:

###High oven fuel
```zenscript
// Adds a valid High Oven fuel (ItemStack fuel, int burnTime, int heatRate)
mods.tsteelworks.highoven.fuel.addFuel(<minecraft:stone>, 20, 1000);

// removes a valid High Oven fuel (ItemStack fuel)
mods.tsteelworks.highoven.fuel.removeFuel(<minecraft:coal:1>);
```
##High oven meltables
```zenscript
// Makes an itemstack meltable in the high oven (ItemStack input, boolean isOre, FluidStack output, int meltTemp)
mods.tsteelworks.highoven.meltable.addMeltable(<minecraft:dirt>, true, <liquid:iron.molten> * 72, 1500);

// Makes an itemstack not smeltable in the high oven
mods.tsteelworks.highoven.meltable.removeMeltable(<minecraft:dirt>);
```

##Notice
Much of the code and structure of this project is heavily based on projects by [SlimeKnights](https://github.com/SlimeKnights).
Even this readme file is partially lifted from said projects.  ^_^
This project is meant to compliment their efforts.