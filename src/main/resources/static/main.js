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

const left = keyboard(37),
  up = keyboard(38),
  right = keyboard(39),
  down = keyboard(40);

let renderer;
let stage;
let state;

function initRenderer(json) {
  renderer = PIXI.autoDetectRenderer(H * json.currentLevel.map.rows, H * json.currentLevel.map.cols, {
    transparent: true,
    backgroundColor: '0x86D0F2'
  });
  document.body.appendChild(renderer.view);

  stage = new PIXI.Container();
}

function parseJSON(response) {
  return response.json()
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
      initRenderer(json);
      updateState(json);
      gameLoop();
    }).catch(function (ex) {
      console.log('parsing failed', ex)
    })
}

function gameLoop() {
  requestAnimationFrame(gameLoop);
  updateState(state);
  renderer.render(stage);
}

function updateState(json) {
  // Clean previous state
  for (let i = stage.children.length - 1; i >= 0; i--) { stage.removeChild(stage.children[i]);}


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


