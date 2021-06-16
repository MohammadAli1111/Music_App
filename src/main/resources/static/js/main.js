var track_index = 0;
var isPlaying = false;
var updateTimer;
// Create new audio element
var audio = document.createElement('audio');
var curr_time = document.querySelector(".current-time");
// button
var playpause_btn = document.getElementById('playpause-track');
// title name Music label
var title = document.getElementById('title');
var seek_slider = document.querySelector(".seek_slider");
var volume_slider = document.querySelector(".volume_slider");
var track_list = [];

function inti(){
  var paths = document.querySelectorAll('.path');
  for (var i=0;i<paths.length;i++){
    track_list[i]= {
      name:paths.item(i).textContent,
      path: "http://localhost:8080/play/"+paths.item(i).id
    }
  }
}

inti();

function loadTrack(track_index) {

  clearInterval(updateTimer);
  resetValues();
  audio.src = track_list[track_index].path;
  title.textContent = track_list[track_index].name;
  audio.load();
  setVolume();
  updateTimer = setInterval(seekUpdate, 1000);
  audio.addEventListener("ended", nextTrack);
 
}

function selectfromList(index){
  stop();
  clearInterval(updateTimer);
  resetValues();
  track_index=index-1;
  audio.src = track_list[index-1].path;
  title.textContent = track_list[index-1].name;
  audio.load();
  setVolume();
  updateTimer = setInterval(seekUpdate, 1000);
  audio.addEventListener("ended", nextTrack);

  playTrack();
}
function resetValues() {
    title.textContent = "Music Name: ";
  curr_time.textContent = "00:00";
  seek_slider.value = 0;
    volume_slider.value=100;
}

// Load the first track in the tracklist
loadTrack(track_index);
function playpauseTrack() {
  if (!isPlaying) 
	playTrack();
  else 
	pauseTrack();
}

function playTrack() {
  audio.play();
  isPlaying = true;
  playpause_btn.innerHTML = '<i class="fa fa-pause"></i>';
}

function pauseTrack() {
  audio.pause();
  isPlaying = false;
  playpause_btn.innerHTML = '<i class="fa fa-play"></i>';;
}


function nextTrack() {
  if (track_index < track_list.length - 1)
    track_index += 1;
  else track_index = 0;
  loadTrack(track_index);
  playTrack();
}

function prevTrack() {
  if (track_index > 0)
    track_index -= 1;
  else track_index = track_list.length;
  loadTrack(track_index);
  playTrack();
}

function stop(){
    pauseTrack();
    audio.currentTime=0;
    resetValues();
}

function seekTo() {
  var seekto = audio.duration * (seek_slider.value / 100);
  audio.currentTime = seekto;
}

function seekUpdate() {
  var seekPosition = 0;

  if (!isNaN(audio.duration)) {
    seekPosition = audio.currentTime * (100 / audio.duration);

    seek_slider.value = seekPosition;

    var currentMinutes = Math.floor(audio.currentTime / 60);
    var currentSeconds = Math.floor(audio.currentTime - currentMinutes * 60);
    var durationMinutes = Math.floor(audio.duration / 60);
    var durationSeconds = Math.floor(audio.duration - durationMinutes * 60);

    if (currentSeconds < 10) { currentSeconds = "0" + currentSeconds; }
    if (durationSeconds < 10) { durationSeconds = "0" + durationSeconds; }
    if (currentMinutes < 10) { currentMinutes = "0" + currentMinutes; }
    if (durationMinutes < 10) { durationMinutes = "0" + durationMinutes; }

   // curr_time.textContent = currentMinutes + ":" + currentSeconds;
    curr_time.textContent = durationMinutes + ":" + durationSeconds;
      //total_duration.textContent = durationMinutes + ":" + durationSeconds;
  }
}
function setVolume() {
  audio.volume = volume_slider.value / 100;
}

//read url image
function readURL(input){

	if(input.files && input.files[0]){
		var  reader = new FileReader();
		reader.onload=function (e){
			$('#Idimge').attr('src',e.target.result);
		}
		reader.readAsDataURL(input.files[0]);
	}

}
function readURLAdmin(){
const input=document.getElementById("Idfile");
  if(input.files && input.files[0]){
    var  reader = new FileReader();
    reader.onload=function (e){
      $('#Idimge').attr('src',e.target.result);
    }
    reader.readAsDataURL(input.files[0]);
  }

}