import React, { useState, useEffect } from "react";
import { IoVolumeHigh, IoVolumeMute } from "react-icons/io5";
import "./MusicControl.css";

const MusicControl = () => {
  const [isMuted, setIsMuted] = useState(false);
  const [audioElement, setAudioElement] = useState(null);

  useEffect(() => {
    const audio = document.getElementById("bgm");
    if (audio) {
      setAudioElement(audio);
      setIsMuted(audio.muted);
      audio.play().catch(error => {
        console.log("Browser blocked autoplay, waiting for user interaction.");
      });
    }
  }, []);

  const toggleMusic = () => {
    if (audioElement) {
      if (audioElement.paused) {
        audioElement.play();
      }
      const nextMuteState = !isMuted;
      audioElement.muted = nextMuteState;
      setIsMuted(nextMuteState);
    }
  };

  if (!audioElement) return null;

  return (
    <button 
      className={`music-toggle-btn ${isMuted ? "muted" : ""}`} 
      onClick={toggleMusic}
      title={isMuted ? "Unmute Music" : "Mute Music"}
    >
      {isMuted ? <IoVolumeMute size={24} /> : <IoVolumeHigh size={24} />}
    </button>
  );
};

export default MusicControl;