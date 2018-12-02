class SoundManager {

    Sound   bullet,
            playerHit,
            playerDead,
            centipedeHit,
            centipedeDead,
            spiderHit,
            spiderDead;

    SoundManager() {
        bullet = new Sound("sounds/bullet.wav");
        playerHit = new Sound("sounds/playerHit.wav");
        playerDead = new Sound("sounds/playerDead.wav");
        centipedeHit = new Sound("sounds/centipedeHit.wav");
        centipedeDead = new Sound("sounds/centipedeDead.wav");
        spiderHit = new Sound("sounds/spiderHit.wav");
        spiderDead = new Sound("sounds/spiderDead.wav");
    }
}
