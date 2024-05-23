"use strict";

(function() { 
const body = document.querySelector('body');
const swapi = document.querySelector('div[data-cmp-is="swapi"]');
const radios = swapi.querySelectorAll('input[type=radio]');
const button = swapi.querySelector('button');
const spinner = button.querySelector('.spinner');

if (swapi) {
var target = button.nextElementSibling;
var level = 0;
button.addEventListener('click', function(){
    buttonBusy();
    console.log(target);
    checkLevel();
    getData(level, target);
});

}

async function getData(ial,target) {
    var response;
    var data;

    response = await fetch('/bin/poc/starwars?level=' + ial);
    data = await response.json();

    var code = target.querySelector('code');
    code.innerText = JSON.stringify(data, null, 2);
    buttonReady();
}

function checkLevel() {
    radios.forEach( i => {
        if (i.checked) {
            level = i.value;
        }
    });
    console.log(level);
}

function buttonBusy() {
    button.setAttribute('disabled', 'disabled');
    spinner.classList.remove('is-hidden');
}

function buttonReady() {
    button.removeAttribute('disabled');
    spinner.classList.add('is-hidden');
}
  
})();


