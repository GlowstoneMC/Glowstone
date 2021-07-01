const fs = require("fs");
const yaml = require("yaml");

const file = fs.readFileSync("./recipes.yml", "utf8");

let yml = yaml.parse(file);

let out = {};

for(key in yml) {
  out[key] = [];
  yml[key].forEach(recipe=>{
    let r = {};
    let ingredients;

    r.result = {type:recipe.result.type};
    if(recipe.result.amount) r.result.amount = recipe.result.amount;
    if(recipe.shape) r.shape = recipe.shape;
    if(recipe.xp) r.xp = recipe.xp;

    if(recipe.ingredients) {
      ingredients = key == "shapeless" ? [] : {};

      for(i in recipe.ingredients) {
        ingredients[i] = {type:recipe.ingredients[i].type};
        if(recipe.ingredients[i].amount) ingredients[i].amount = recipe.ingredients[i].amount;
      }
      r.ingredients = ingredients
    }

    if(recipe.input) {
      r.input = {type:recipe.input.type};
      if(recipe.input.amount) r.input.amount = recipe.input.amount;
    }

    out[key].push(r);
  });
}

fs.writeFile("./new-yml.yml",yaml.stringify(out),()=>{});

