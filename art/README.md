This art flow allows you to create sprites via aseprite and export them with `export_tags.sh` or `export_and_update.sh`.


## Exporting tiles and generating an atlas

This assumes you are using **TexturePacker** for packing textures. If not, then the `update_atlas.sh` script will need updated to use the texture packer you are using.

To export tags from an aseprite file:
* Save your `*.ase` or `*.aseprite` file in the `ase/` directory
* Run the script `./export_tags.sh` which will export all the tags in the aseprite file and place them in the `export_tiles/` directory.
* Alternatively pass in a single file name to export all the tags in that aseprite file. `./export_tags.sh mySprite.aseprite`.
* Run the `packTextures` gradle task from the `texturepacker` gradle plugin with the input directory pointed at `art/export_tiles/` and the output directory set to `src/commonMain/resources/` 

## Exporting tiles for use with **LDtk**
Open the `tiles.aseprite` file which will contain all the tiles used in the **LDtk** project. 
Exporting the tiles to `src/commonMain/resources/tiles.png` to be used by the **LDtk** project and **Korge**.


## Misc
If you need to change settings on the **TexturePacker** file, then open up the `tiles.tps` file and edit the settings there. It is currently set up to handle exporting correctly for pixel art in Korge. This will probably work for most art styles.