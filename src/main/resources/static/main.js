'use strict';

const H = 32;


PIXI.loader
  .add("graphic/creature/bat.png")
  .add("graphic/cell/blank.png")
  .add("graphic/cell/wall.png")
  .add("graphic/cell/door.png")
  .add("graphic/cell/down_stairs.png")
  .add("graphic/cell/null.png")
  .add("graphic/cell/opened_door.png")
  .add("graphic/cell/up_stairs.png")
  .add("graphic/dwelling/rat.png")
  .add("graphic/creature/orc_2.png")
  .add("graphic/hero/hero2.png")
  .load(setup);

//This `setup` function will run when the image has loaded
function setup() {
  fetch('/game-state')
    .then(function (response) {
      return response.json()
    }).then(function (json) {

      var renderer = PIXI.autoDetectRenderer(H * json.currentLevel.map.rows, H * json.currentLevel.map.cols, {
        transparent: true,
        backgroundColor: '0x86D0F2'
      });
      document.body.appendChild(renderer.view);

      var stage = new PIXI.Container();

      let cells = json.currentLevel.map.cells;

      for (let i = 0; i < cells.length; i++)
        for (let j = 0; j < cells[i].length; j++) {

          let blank = new PIXI.Sprite(
            PIXI.loader.resources[cells[i][j].imagePath].texture
          );

          blank.x = i * H;
          blank.y = j * H;

          stage.addChild(blank);
        }

      let dwellings = json.currentLevel.dwellings;

      dwellings.forEach((dwelling) => {

        let picture = new PIXI.Sprite(
          PIXI.loader.resources[dwelling.imagePath].texture
        );

        picture.x = dwelling.location.x * H;
        picture.y = dwelling.location.y * H;

        stage.addChild(picture);
      });

      let creatures = json.currentLevel.creatures;

      creatures.forEach((creature) => {

        let picture = new PIXI.Sprite(
          PIXI.loader.resources[creature.imagePath].texture
        );

        picture.x = creature.x * H;
        picture.y = creature.y * H;

        stage.addChild(picture);
      });

      //Render the stage
      renderer.render(stage);
    }).catch(function (ex) {
      console.log('parsing failed', ex)
    })

}


