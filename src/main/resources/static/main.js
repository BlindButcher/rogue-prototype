'use strict';

const H = 32;
const NULL_CELL = 'graphic/cell/null.png';
let viewportX = 0, viewportY = 0;
let viewHeight = 26, viewportWidth = 26;
let worldHeight, worldWidth;

PIXI.loader
  .add("graphic/creature/bat.png")
  .add("graphic/cell/blank.png")
  .add("graphic/cell/wall.png")
  .add("graphic/cell/door.png")
  .add("graphic/cell/down_stairs.png")
  .add("graphic/cell/null.png")
  .add("graphic/cell/opened_door.png")
  .add("graphic/cell/up_stairs.png")
  .add("graphic/cell/start_up.png")
  .add("graphic/dwelling/rat.png")
  .add("graphic/creature/orc_2.png")
  .add("graphic/hero/hero2.png")
  .add("graphic/cell/closed_door.png")
  .add("graphic/creature/troll_v2_32.png")
  .load(setup);

const left = keyboard(37),
  up = keyboard(38),
  right = keyboard(39),
  down = keyboard(40);

let renderer;
let stage;
let state;

function findHero(state) {
  return state.currentLevel.creatures.find((c) => c.name === 'HERO');
}

function initRenderer(json) {
  renderer = PIXI.autoDetectRenderer(H * json.currentLevel.map.rows, H * json.currentLevel.map.cols);
  document.body.appendChild(renderer.view);

  stage = new PIXI.Container();
}

function parseJSON(response) {
  return response.json()
}

function inMapRange(state, x, y) {
  let row = state.currentLevel.map.rows;
  let cols = state.currentLevel.map.cols;
  return !((x < 0 || x >= row)
  || (y < 0 || y >= cols));
}

function handleDeath() {
  stage.destroy(true);

  var div = document.createElement("div");
  div.style.width = "100px";
  div.style.height = "100px";
  div.style.background = "red";
  div.style.color = "white";
  div.innerHTML = "You lose.";

  document.getElementById("main").appendChild(div);


}

function handleMove(delta) {
  fetch("/move", {
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },
    method: "POST",
    body: JSON.stringify(delta)
  })
    .then(parseJSON)
    .then((json) => {
      state = json;

      let hero = findHero(state);
      if (state.result === 'LOSE') {
        handleDeath();
      }
      viewportX = hero.x - viewportWidth / 2;
      viewportY = hero.y - viewHeight / 2;

    }).catch(function (ex) {
      console.log('parsing failed', ex)
    });
}

left.press = () => handleMove({ dx: -1, dy: 0 });
right.press = () => handleMove({ dx: 1, dy: 0 });
down.press = () => handleMove({ dx: 0, dy: 1 });
up.press = () => handleMove({ dx: 0, dy: -1 });

//This `setup` function will run when the image has loaded
function setup() {
  fetch('/game-state')
    .then(function (response) {
      return response.json()
    }).then(function (json) {
      state = json;
      let hero = findHero(state);
      viewportX = hero.x - viewportWidth / 2;
      viewportY = hero.y - viewHeight / 2;
      initRenderer(json);
      updateState(json);
      gameLoop();
    }).catch(function (ex) {
      console.log('parsing failed', ex)
    })
}

function gameLoop() {
  if (state.result !== 'LOSE') {
    requestAnimationFrame(gameLoop);
    updateState(state);
    renderer.render(stage);
  }
}

function updateState(json) {

  // Clean previous state
  for (let i = stage.children.length - 1; i >= 0; i--) { stage.removeChild(stage.children[i]);}


  let cells = json.currentLevel.map.cells;

  for (let i = viewportX; i < viewportX + viewportWidth; i++)
    for (let j = viewportX; j < viewportY + viewHeight; j++) {

      let imagePath = inMapRange(json, i, j) ? cells[i][j].imagePath : NULL_CELL;

      let blank = new PIXI.Sprite(
        PIXI.loader.resources[imagePath].texture
      );

      blank.x = (i - viewportX) * H;
      blank.y = (j - viewportY) * H;

      let aware = inMapRange(json, i, j) ? json.currentLevel.map.aware[i][j] : false;
      let sees = inMapRange(json, i, j) ? json.heroSees[i][j] : false;

      if (aware) {
        if (!sees) {
          let greyFilter = new PIXI.filters.ColorMatrixFilter();
          greyFilter.greyscale(0.5, false);
          blank.filters = [greyFilter];
        }
        stage.addChild(blank);
      }
    }

  let dwellings = json.currentLevel.dwellings;

  /* dwellings.forEach((dwelling) => {

   let picture = new PIXI.Sprite(
   PIXI.loader.resources[dwelling.imagePath].texture
   );

   picture.x = dwelling.location.x * H + viewportX * H;
   picture.y = dwelling.location.y * H + viewportY * H;

   stage.addChild(picture);
   });*/

  let creatures = json.currentLevel.creatures;

  creatures.forEach((creature) => {
    let sees = json.heroSees[creature.x][creature.y];
    if (sees) {
      let picture = new PIXI.Sprite(
        PIXI.loader.resources[creature.imagePath].texture
      );

      picture.x = (creature.x - viewportX) * H;
      picture.y = (creature.y - viewportY) * H;

      stage.addChild(picture);
    }
  });
}

function keyboard(keyCode) {
  var key = {};
  key.code = keyCode;
  key.isDown = false;
  key.isUp = true;
  key.press = undefined;
  key.release = undefined;
  //The `downHandler`
  key.downHandler = function (event) {
    if (event.keyCode === key.code) {
      if (key.isUp && key.press) key.press();
      key.isDown = true;
      key.isUp = false;
    }
    event.preventDefault();
  };

  //The `upHandler`
  key.upHandler = function (event) {
    if (event.keyCode === key.code) {
      if (key.isDown && key.release) key.release();
      key.isDown = false;
      key.isUp = true;
    }
    event.preventDefault();
  };

  //Attach event listeners
  window.addEventListener(
    "keydown", key.downHandler.bind(key), false
  );
  window.addEventListener(
    "keyup", key.upHandler.bind(key), false
  );
  return key;
}


