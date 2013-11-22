package package_my_bench;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 * Game engine
 * @author Robert Brestle
 */
public class BenchGame extends JFrame implements Runnable, KeyListener, MouseListener, MouseMotionListener {
    
    public BenchGame(int x, int y) {
        this.ene = new ArrayList<>();
        this.brd = new ArrayList<>();
        this.egg = new ArrayList<>();
        this.shot = new ArrayList<>();
        this.explo = new ArrayList<>();
        
        //screen size
        screenX = x;
        screenY = y;
        
        init();
    }

    private void init() {
        //initialize KeyListener
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        //sets up img for the stage
        img = new BufferedImage(screenX, screenY, BufferedImage.TYPE_INT_ARGB);
        img.createGraphics();
        buffer = img.getGraphics();
        
        background = new BufferedImage(screenX, screenY, BufferedImage.TYPE_INT_ARGB);
        
        //load images
        titles = new BufferedImage[5];
        titles[0] = getPic("pictures/DeerStudios.png"); //splash screen
        titles[1] = getPic("pictures/title.png");   //main title
        titles[2] = getPic("pictures/directions2.png");
        titles[3] = getPic("pictures/game.png");
        titles[4] = getPic("pictures/hardmode.png");
        
        cursor = new BufferedImage[4];
        cursor[0] = getPic("pictures/cursor.png");
        cursor[1] = getPic("pictures/bench.png");
        cursor[2] = getPic("pictures/ibench.png");
        cursor[3] = getPic("pictures/gun.png");
        
        icons = new BufferedImage[8];
        icons[0] = getPic("pictures/heart.png");
        icons[1] = getPic("pictures/bulletmeter.png");
        icons[2] = getPic("pictures/h.png");
        icons[3] = getPic("pictures/noth.png");
        icons[4] = getPic("pictures/musicon.png");
        icons[5] = getPic("pictures/musicoff.png");
        icons[6] = getPic("pictures/networkon.png");
        icons[7] = getPic("pictures/networkoff.png");
        
        enemies = new BufferedImage[4];
        enemies[0] = getPic("pictures/man.png");
        enemies[1] = getPic("pictures/bad.png");
        enemies[2] = getPic("pictures/squid.png");
        enemies[3] = getPic("pictures/robot.png");
        
        birds = new BufferedImage[4];
        birds[0] = getPic("pictures/birdright.png");
        birds[1] = getPic("pictures/birdleft.png");
        birds[2] = getPic("pictures/evilbirdright.png");
        birds[3] = getPic("pictures/evilbirdleft.png");
        
        eggs = new BufferedImage[4];
        eggs[0] = getPic("pictures/egg.png");
        eggs[1] = getPic("pictures/hpegg.png");
        eggs[2] = getPic("pictures/ammoegg.png");
        eggs[3] = getPic("pictures/eggnade.png");
        
        weapons = new BufferedImage[1];
        weapons[0] = getPic("pictures/bullet.png");
        
        ready = false;
        showScores = false;
        stage = -1;
        gunOut = false;
        server = "default";
        
        //begin stage thread
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        
        background = titles[0];
        repaint();
        
        if(!debug) {
            try {
                Thread.sleep(1000);     //1.0 seconds
            }catch (InterruptedException e) { }
            playSound("sounds/hurr.wav");
            try {
                Thread.sleep(2500);     //2.5 seconds
            } catch (InterruptedException e) {}
        }
        stage++;
        
        //game loop
        while (true) {
//            if(backgroundClip != null) {
//                stopMusic();
//            }
            
            if(stage >= 3 /*&& mouseX != -1*/) {
                reset();
                mouseX = mouseY = -1;
            }
            
                if(stage == 0) {
//                    if(soundOn) {
//                        loadMusic("sounds/battleloop.wav");
//                        playMusic();
//                    }
                    background = titles[1];
                    mouseX = mouseY = -1;
                    
                    repaint();
                    
                    if(name == null) {
                        name = JOptionPane.showInputDialog(null, "Please enter your name:", "Name Entry", JOptionPane.OK_OPTION);
                        if(name == null || name.equals("")) {
                            name = "Default";
                        }
                        if(name.equals("drjeffrey")) {
                            debug = true;
                        }
                    }
                }
                
                //TITLE SCREEN
                while(stage == 0) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {}
                    
                    //reset to title screen
                    if(mouseX != -1) {
                        background = titles[1];
                    }
                    
                    //toggle music
                    if(mouseX > 600 && mouseX < 632 && mouseX > 30 && mouseY < 62) {
                        soundOn = !soundOn;
//                        if(!soundOn && backgroundClip != null) {
//                            stopMusic();
//                        }else {
//                            playMusic();
//                        }
                        mouseX = -1;
                    }
                    //toggle difficulty
                    if(mouseX > 550 && mouseX < 582 && mouseX > 30 && mouseY < 62) {
                        hardmode = !hardmode;
                        mouseX = -1;
                    }
                    //toggle networking
                    if(mouseX > 600 && mouseX < 632 && mouseX > 80 && mouseY < 112) {
                        networking = !networking;
                        if(networking) {
                            server = JOptionPane.showInputDialog(null, "Please enter a custom server IP and port (i.e. 127.0.0.1:9001). Press Enter to use the default server.","Server Selection", JOptionPane.OK_OPTION);
                            if(server == null || server.equals("")) {
                                server = "default";
                            }
                        }
                        mouseX = -1;
                    }
                    
                    //play game
                    if((mouseX > 86 && mouseX < 192) && (mouseY > 396 && mouseY < 432)) {
//                        if(backgroundClip != null) {
//                            stopMusic();
//                        }
                        stage++;
                        mouseX = -1;
                        if(hardmode) {
                            background = titles[4];
                        }else {
                            background = titles[3];
                        }
                        
                        levelSetup();

//                        if(musicOn) {
//                            loadMusic("sounds/battleloop.wav");
//                            loopMusic(100);
//                        }
                        
                    //directions
                    }else if((mouseX > 343 && mouseX < 592) && (mouseY > 424 && mouseY < 461)) {
                        background = titles[2];
                        mouseX = -1;
                        
                    //high scores
                    }else if((mouseX > 335 && mouseX < 604) && (mouseY > 371 && mouseY < 407)) {
                        mouseX = -1;
                        if(!networking) {
                            JOptionPane.showConfirmDialog(null, "Please enable networking to check scores.", "Error", JOptionPane.PLAIN_MESSAGE);
                        }else {
                            showScores = true;

                            normScore = new ArrayList<>();
                            hardScore = new ArrayList<>();

                            HighScore hs = new HighScore(server);
                            Thread t = new Thread(hs);
                            t.start();

                            repaint();

                            while(true) {
                                //delay until user clicks to next screen
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException e) {}
                                if(mouseX != -1 || hs.getActive() == 6)
                                    break;

                                //wait for server communication
                                if(hs.getActive() <= 5) {
                                    switch(hs.getActive()) {
                                        case 0:
                                        case 2:
                                            hs.retrieveList();
                                            break;
                                        case 4:
                                            hardScore = hs.getList(true);
                                            normScore = hs.getList(false);
                                            ready = true;
                                            hs.setActive(5);
                                            break;
                                    }
                                }
                                if(hs.getActive() == 5)
                                    repaint();

                            }//while stage!=4

                            //stop thread/delete hs object
                            hs = null;
                            showScores = false;
                            ready = false;
                            mouseX = -1;
                        }
                    //change name
                    }else if((mouseX > 550 && mouseX < 620) && (mouseY > 65 && mouseY < 75)) {
                        name = JOptionPane.showInputDialog(rootPane, "Please enter your name:", "Name Entry", JOptionPane.OK_OPTION);
                        if(name == null || "".equals(name)) {
                            name = "Default";
                        }
                        mouseX = -1;
                    }
                    
                    
                    repaint();
                }//stage0 while
            
            
            
            
            
            
            
            //game loop
            while(stage == 1) {
                //thread sleep
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {}
                
                //bullets
                regulateShots();
                
                //egg movement
                regulateEggs();
                
                //explosions
                regulateExplosions();
                
                //bird movement
                regulateBirds();
                
                //enemy movement
                regulateEnemies();
                
                //
                levelManagement();
                
                repaint();
            }//game level
            
            
            mouseX = -1;
            
            if(!networking) {
                stage = 2;
                while(stage != 4) {
                    //delay until user clicks to next screen
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {}
                    if(mouseX != -1)
                        stage = 4;
                }
            }
            
            //game over
            if(stage == 2 && networking) {
                
//                if(backgroundClip != null) {
//                    stopMusic();
//                }
                
                normScore = new ArrayList<>();
                hardScore = new ArrayList<>();
                
                HighScore hs = new HighScore(server);
                Thread t = new Thread(hs);
                t.start();

                while(stage != 4) {
                    //delay until user clicks to next screen
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {}
                    if(mouseX != -1)
                        stage = 4;

                    //wait for server communication
                    if(hs.getActive() <= 4) {
                        switch(hs.getActive()) {
                            case 0:
                                hs.addToList(name, score, hardmode);
                                break;
                            case 2:
                                hs.retrieveList();
                                stage = 3;
                                break;
                            case 4:
                                hardScore = hs.getList(true);
                                normScore = hs.getList(false);
                                hs.setActive(5);
                                break;
                        }
                    }
                    if(hs.getActive() == 5)
                        repaint();
                    
                }//while stage!=4
                
                //stop thread/delete hs object
                hs = null;
                
            }
            
            

            
            mouseX = mouseY = -1;
        }//game while
    }//run
    
    void levelSetup() {
        if(hardmode) {
            ammo = 50;
            gunOut = true;
        }else {
            ammo = 0;
            gunOut = false;
        }
        
        generateNewEnemy(10);
        score = 5;
        health = 10;
    }
    
    void reset() {
        stage = 0;
        ene.clear();
        brd.clear();
        egg.clear();
        shot.clear();
        explo.clear();
        gunOut = false;
    }
    
    
    void generateNewEnemy(int j) {
        for(int i = 0; i < j; ++i) {
            enemy temp = new enemy();
            temp.isSitting = false;
            temp.isGood = (gen.nextInt(100) < 50 ? true : false);

            temp.right = (gen.nextInt(100) < 50 ? true : false);
            if(temp.right)
                temp.x = -80;
            else temp.x = 640;
            temp.y = gen.nextInt(445);//+ 15;
            
            temp.delay = gen.nextInt(500);
            
            if(hardmode) {
                temp.speed = gen.nextInt(4)+10;
            }else {
                temp.speed = gen.nextInt(8)+1;
            }
            
            ene.add(temp);
        }
    }
    
    void generateNewBird(int j) {
        for(int i = 0; i < j; ++i) {
            bird temp = new bird();

            temp.right = (gen.nextInt(100) < 50 ? true : false);
            if(temp.right)
                temp.x = -80;
            else temp.x = 640;
            temp.y = gen.nextInt(50)+ 15;
            
            temp.delay = gen.nextInt(500);
            
            if(hardmode) {
                temp.speed = gen.nextInt(4)+10;
            }else {
                temp.speed = gen.nextInt(8)+1;
            }
            
            temp.dropLocation = gen.nextInt(640);
            
            brd.add(temp);
        }
    }
                        //position, eggx, eggy
    void generateNewEgg(int j, int a, int b) {
        for(int i = 0; i < j; ++i) {
            projectile temp = new projectile();

            temp.x = a;
            temp.y = b;
            
            if(hardmode) {
                temp.speed = gen.nextInt(4)+10;
                
                temp.type = gen.nextInt(200) < 100 ? 3 : gen.nextInt(100) < 25 ? 1 : 2;
            }else {
                temp.speed = gen.nextInt(8)+1;
                // 0 = regular, 1 = hp, 2 = ammo, 3 = grenade(bad)
                temp.type = gen.nextInt(300) < 200 ? 0 : gen.nextInt(100) < 25 ? 3 : gen.nextInt(100) < 50 ? 1 : 2;

            }
            
            
            if(temp.type == 3) {
                temp.explY = gen.nextInt(300) + 100;
            }
            
            egg.add(temp);
        }
    }
                //position x, y, direcion
    void fireShot(int a, int b, int d) {
        projectile temp = new projectile();
        
        temp.x = a;
        temp.y = b;
        temp.direction = d;
        temp.speed = 10;
        
        shot.add(temp);
        
        ammo--;
        if(ammo < 1) {
            gunOut = false;
        }
    }
    
    //KeyListener methods:
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        //debugging options
        if(debug) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_F4:
                    god = !god;
                    break;
                case KeyEvent.VK_F5:
                    score+=5;
                    break;
                case KeyEvent.VK_F6:
                    stage = 2;
                    break;
                case KeyEvent.VK_F7:
                    System.out.println(stage);
                    break;
            }
        }
        
        if(ammo > 0 && stage == 1) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                    fireShot(oX, oY, 0);
                    break;
                case KeyEvent.VK_A:
                    fireShot(oX, oY, 1);
                    break;
                case KeyEvent.VK_S:
                    fireShot(oX, oY, 2);
                    break;
                case KeyEvent.VK_D:
                    fireShot(oX, oY, 3);
                    break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:    //quit via ESC key
                playSound("sounds/clank.wav");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {}
                System.exit(1337);
                break;
            default:
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(stage > -1) {
            mouseX = e.getX();
            mouseY = e.getY();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseDragged(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {
        if(stage > -1) {
            oX = e.getX();
            oY = e.getY();
        }
    }
    
    
    @Override
    public void paint(Graphics page) {
        super.paint(buffer);
        
        if(stage == -1) {
            buffer.drawImage(background, 0, 0, 640, 480, this);
        }else if(stage == 0) {
            //display score screen
            if(showScores) {
                buffer.setColor(Color.BLACK);
                buffer.fillRect(0,0,screenX, screenY);
                
                if(ready) { //if scores have been successfully retreived, display them
                    buffer.setColor(Color.WHITE);
                    buffer.drawString("Normal High Scores:", 300, 100);
                    buffer.drawLine(300, 102, 400, 102);

                    for(int i = 0; i < 20; i+=2) {
                        buffer.drawString(normScore.get(i) + ": " + normScore.get(i+1), 300, 120+(i*7));
                    }

                    buffer.drawString("Hardmode High Scores:", 300, 300);
                    buffer.drawLine(300, 302, 400, 302);
                    for(int i = 0; i < 20; i+=2) {
                        buffer.drawString(hardScore.get(i) + ": " + hardScore.get(i+1), 300, 320+(i*7));
                    }
                }
            
            }else { //display title
                buffer.drawImage(background, 0, 0, 640, 480, this);
            
                //selection rectangles
                buffer.setColor(Color.YELLOW);
                //play
                if((oX > 86 && oX < 192) && (oY > 396 && oY < 432))
                    buffer.drawRect(86, 396, 106, 36);
                //high scores
                if((oX > 335 && oX < 604) && (oY > 371 && oY < 407))
                    buffer.drawRect(335, 371, 270, 35);
                //directions
                if((oX > 343 && oX < 592) && (oY > 424 && oY < 461))
                    buffer.drawRect(343, 424, 249, 36);
                //change name
                if((oX > 550 && oX < 620) && (oY > 65 && oY < 75))
                    buffer.drawRect(550, 66, 80, 10);

                buffer.setColor(Color.BLACK);
                buffer.drawString("Change Name", 550, 76);
                //music icon
                buffer.drawImage(soundOn ? icons[4] : icons[5],600,30,this);
                //hardmode icon
                buffer.drawImage(hardmode ? icons[2] : icons[3] ,550,30,this);
                //networking icon
                buffer.drawImage(networking ? icons[6] : icons[7], 600, 80, this);
            }
            
            //cursor
            buffer.drawImage(cursor[0],oX,oY,this);
        }else if(stage == 1) {
            buffer.drawImage(background, 0, 0, 640, 480, this);            
            buffer.drawImage(hardmode ? cursor[2] : cursor[1], oX - 40, oY - 32, this);
            
            //draw enemies
            for(int i = 0; i < ene.size(); ++i) {
                if(ene.get(i).delay < 0) {
                    buffer.drawImage(ene.get(i).isGood? (hardmode ? enemies[2] : enemies[0]) : (hardmode ? enemies[3] : enemies[1]), ene.get(i).x, ene.get(i).y, this);
                }
            }
            
            //draw birds
            for(int i = 0; i < brd.size(); ++i) {
                if(brd.get(i).delay < 0) {
                    buffer.drawImage(brd.get(i).right ? (hardmode ? birds[2] : birds[0]) : (hardmode ? birds[3] : birds[1]), brd.get(i).x, brd.get(i).y, this);
                }
            }
            
            //draw eggs
            for(int i = 0; i < egg.size(); ++i) {
                buffer.drawImage(eggs[egg.get(i).type], egg.get(i).x, egg.get(i).y, this);
            }
            
            //draw shots
            for(int i = 0; i < shot.size(); ++i) {
                buffer.drawImage(weapons[0], shot.get(i).x, shot.get(i).y, this);
            }
            
            if(gunOut) {
                buffer.drawImage(cursor[3], oX, oY, 40, 24, this);
            }
            
            //draw explosions
            for(int i = 0; i < explo.size(); ++i) {
                buffer.setColor(new Color(gen.nextInt(255),gen.nextInt(100),gen.nextInt(100)));
                buffer.fillOval(explo.get(i).x, explo.get(i).y, explo.get(i).radius, explo.get(i).radius);
            }
            
            buffer.drawImage(icons[0], 10, 35, this);
            buffer.drawImage(icons[1], 10, 50, this);
            buffer.setColor(Color.BLACK);
            buffer.fillRect(31, 38, health * 10 , 10);
            buffer.fillRect(31, 53, ammo, 10);
            buffer.drawString("Score: "+score, 10, 75);
            if(hardmode) {
                buffer.setColor(Color.CYAN);
            }else {
                buffer.setColor(Color.RED);
            }
            buffer.fillRect(30, 37, health * 10, 10);
            buffer.setColor(Color.GREEN);
            buffer.fillRect(30,52, ammo < 100 ? ammo : 100, 10);
            
            if(debug) {
                buffer.setColor(Color.BLACK);
                buffer.drawString("E:"+ene.size(), 200, 160);
                buffer.drawString("B:"+brd.size(), 200, 180);
                buffer.drawString("EG:"+egg.size(), 200, 200);
                buffer.drawString("Ex:"+explo.size(), 200, 220);
            }
            
        }else {
            buffer.setColor(Color.BLACK);
            buffer.fillRect(0,0,640,480);
            buffer.setColor(Color.WHITE);
            buffer.drawString("GAME OVER", 100, 200);
            buffer.drawString("SCORE: "+score, 100, 210);
            buffer.drawString("Death Snapshot", 140, 240);
            buffer.drawLine(140, 242, 240, 242);
            buffer.drawString("Ammo: " + ammo, 140, 260);
            buffer.drawString(hardmode ? "Aliens: " + ene.size(): "People: " + ene.size(), 140, 280);
            buffer.drawString(hardmode ? "Demon Birds: " + brd.size() : "Birds: " + brd.size(), 140, 295);
            buffer.drawString(hardmode ? "White Orbs of Mistrust: " + egg.size() : "Eggs: " + egg.size(), 140, 310);
            
            
            
            if(stage == 3) {
                buffer.drawString("Normal High Scores:", 300, 100);
                buffer.drawLine(300, 102, 400, 102);
                
                for(int i = 0; i < 20; i+=2) {
                    buffer.drawString(normScore.get(i) + ": " + normScore.get(i+1), 300, 120+(i*7));
                }

                buffer.drawString("Hardmode High Scores:", 300, 300);
                buffer.drawLine(300, 302, 400, 302);
                for(int i = 0; i < 20; i+=2) {
                    buffer.drawString(hardScore.get(i) + ": " + hardScore.get(i+1), 300, 320+(i*7));
                }
            }
        }
        
        page.drawImage(img, 0, 0, this);
    }
    
    @Override
    public void update(Graphics page) {
        paint(page);
    }
    
    
    
    
    
    
    
    
    
    
    
    void levelManagement() {
        if(hardmode) {
            if(score % 100 == 0) {
                generateNewEnemy(1);
                generateNewBird(1);
                score += 5;
            }
        }else {
            if(score % 1000 == 0) {
                generateNewEnemy(10);
                generateNewBird(5);
                score += 5;
            }
            if(score % 100 == 0) {
                generateNewEnemy(2);
                generateNewBird(1);
                score += 5;
            }
        }
    }//level
    
    void regulateEnemies() {
        for(int i = 0; i < ene.size(); ++i) {
            if(ene.get(i).delay > -1) {
                ene.get(i).delay--;
            }else {
                if((oX < ene.get(i).x + 64) && (oX > ene.get(i).x) &&
                        (oY < ene.get(i).y + 80) && (oY > ene.get(i).y)) {
                    if(ene.get(i).isGood) {
                        score += 5;
                        ene.remove(i);
                        generateNewEnemy(1);
                        if(soundOn) {
                            playSound("sounds/ding.wav");
                        }
                    }else {
                        if(!god) {
                            if(health > 1) {
                                health--;
                                ene.remove(i);
                                generateNewEnemy(1);
                                if(soundOn) {
                                    playSound("sounds/pain.wav");
                                }
                            }else {
                                stage = 2;
                            }
                        }
                    }
                }else {
                    if(ene.get(i).x > -81 && ene.get(i).x < 645) {
                        if(ene.get(i).right) {
                            ene.get(i).x += ene.get(i).speed;
                        }else {
                            ene.get(i).x -= ene.get(i).speed;
                        }
                    }else {
                        ene.remove(i);
                        generateNewEnemy(1);
                    }
                }
            }
        }
    }//regenemies
    
    void regulateBirds() {
        for(int i = 0; i < brd.size(); ++i) {
            if(brd.get(i).delay > -1) {
                brd.get(i).delay--;
            }else {
                if((oX < brd.get(i).x + 32) && (oX > brd.get(i).x) &&
                        (oY < brd.get(i).y + 20) && (oY > brd.get(i).y)) {
                    if(!god) {
                        if(health > 1) {
                            health--;
                            brd.remove(i);
                            if(soundOn) {
                                playSound("sounds/pain.wav");
                            }
                        }else {
                            stage = 2;
                        }
                    }
                }else {
                    if(brd.get(i).x > -81 && brd.get(i).x < 645) {
                        if(brd.get(i).right) {
                            brd.get(i).x += brd.get(i).speed;
                        }else {
                            brd.get(i).x -= brd.get(i).speed;
                        }
                    }else {
                        brd.remove(i);
                        generateNewBird(1);
                    }

                    if((brd.get(i).x > brd.get(i).dropLocation && brd.get(i).right) 
                            || (brd.get(i).x < brd.get(i).dropLocation && !brd.get(i).right)) {
                        generateNewEgg(1,brd.get(i).x,brd.get(i).y);
                        if(brd.get(i).right)
                            brd.get(i).dropLocation = 700;
                        else brd.get(i).dropLocation = -100;
                    }
                }
            }
        }
    }//regbirds
    
    void regulateEggs() {
        for(int i = 0; i < egg.size(); ++i) {
            if(egg.get(i).y < 480) {
                egg.get(i).y += egg.get(i).speed;

                //player interaction
                if((egg.get(i).x + 12 < oX + 40) && (egg.get(i).x + 12 > oX - 40) &&
                    (egg.get(i).y + 16 < oY + 32) && (egg.get(i).y + 16 > oY - 32)) {
                        switch(egg.get(i).type) {
                            case 0://regular egg
                            case 3://grenade
                                if(!god) {
                                    if(health > 1) {
                                        health--;
                                        if(soundOn) {
                                            playSound("sounds/pain.wav");
                                        }
                                    }else {
                                        stage = 2;
                                    }
                                }
                                break;
                            case 1:
                                if(health < 10)
                                    health++;
                                if(health == 10)
                                    score += 5;
                                if(soundOn) {
                                    playSound("sounds/ding.wav");
                                }
                                break;
                            case 2:
                                if(ammo < 100) {
                                    ammo += 10;
                                    if(ammo > 100)
                                        ammo = 100;
                                    
                                    gunOut = true;
                                    if(soundOn) {
                                        playSound("sounds/reload.wav");
                                    }
                                }else if(ammo == 100) {
                                    score += 5;
                                }
                                break;
                        }
                        egg.remove(i);
                }
            }else {
                egg.remove(i);
            }
        }
        
        //grenade egg regulation
        for(int i = 0; i < egg.size(); ++i) {
            //if the egg is a grenade
            if(egg.get(i).type == 3) {
                //if the egg reached the explosion location
                if(egg.get(i).y > egg.get(i).explY) {
                    //create new explosion
                    explosion exp = new explosion();
                    exp.x = egg.get(i).x - 24;
                    exp.y = egg.get(i).y - 16;
                    exp.radius = 64;
                    exp.duration = 25;
                    exp.hurtful = true;
                    
                    explo.add(exp);
                    //remove egg.
                    egg.remove(i);
                    
                    if(soundOn) {
                        playSound("sounds/boom1.wav");
                    }
                }
            }
        }
    }//regeggs
    
    void regulateExplosions() {
        for(int i = 0; i < explo.size(); ++i) {
            //blast hurts player (only once)
            if((oX < explo.get(i).x + explo.get(i).radius) && (oX > explo.get(i).x)
                    && (oY < explo.get(i).y + explo.get(i).radius) && (oY > explo.get(i).y) && explo.get(i).hurtful) {
                explo.get(i).hurtful = false;
                health--;
                if(soundOn) {
                    playSound("sounds/pain.wav");
                }
            }
            //blast kills enemies
            for(int j = 0; j < ene.size(); ++j) {
                if((ene.get(j).x < explo.get(i).x + explo.get(i).radius) && (ene.get(j).x+64 > explo.get(i).x)
                        && (ene.get(j).y < explo.get(i).y + explo.get(i).radius) && (ene.get(j).y+80 > explo.get(i).y)) {
                    ene.remove(j);
                    generateNewEnemy(1);
                }
            }
            
            
            
            //explosion duration timer
            explo.get(i).duration--;
            if(explo.get(i).duration < 0) {
                explo.remove(i);
            }
        }
    }
    
    void regulateShots() {
        for(int i = 0; i < shot.size(); ++i) {
            if(shot.get(i).x < -10 || shot.get(i).x > 650 || shot.get(i).y < -10 || shot.get(i).y > 490) {
                shot.remove(i);
            }else {
                boolean stopped = false;

                switch(shot.get(i).direction) {
                    case 0://w
                        shot.get(i).y -= shot.get(i).speed;
                        break;
                    case 1://a
                        shot.get(i).x -= shot.get(i).speed;
                        break;
                    case 2://s
                        shot.get(i).y += shot.get(i).speed;
                        break;
                    case 3://d
                        shot.get(i).x += shot.get(i).speed;
                        break;
                }
                int tx = shot.get(i).x + 2;
                int ty = shot.get(i).y + 2;

                //egg collision:
                for(int j = 0; j < egg.size() && !stopped; ++j) {
                    if((tx < egg.get(j).x + 24) && (tx > egg.get(j).x) &&
                        (ty < egg.get(j).y + 32) && (ty > egg.get(j).y)) {
                            egg.remove(j);
                            shot.remove(i);
                            stopped = true;

                            if(soundOn) {
                                playSound("sounds/pop.wav");
                            }
                            break;
                    }
                }
                if(stopped) {
                    continue;
                }

                //bird collision
                for(int j = 0; j < brd.size() && !stopped; ++j) {
                    if((tx < brd.get(j).x + 32) && (tx > brd.get(j).x) &&
                        (ty < brd.get(j).y + 20) && (ty > brd.get(j).y)) {
                            brd.remove(j);
//                            generateNewBird(1);
                            shot.remove(i);
                            stopped = true;

                            if(soundOn) {
//                                playSound("sounds/pop.wav");
                                playSound("sounds/bird.wav");
                            }
                            break;
                    }
                }

                if(stopped) {
                    continue;
                }

                //enemy collision
                for(int j = 0; j < ene.size(); ++j) {
                    if((tx < ene.get(j).x + 64) && (tx > ene.get(j).x) &&
                        (ty < ene.get(j).y + 80) && (ty > ene.get(j).y)) {
                            ene.remove(j);
                            generateNewEnemy(1);
                            shot.remove(i);

                            if(soundOn) {
                                playSound("sounds/pop.wav");
                            }

                            break;
                    }
                }
            }
        }
    }//end regshots
    
    private synchronized void playSound(final String url) {
        URL soundURL = getClass().getClassLoader().getResource(url);
        Line.Info linfo = new Line.Info(Clip.class);
        try {
            Clip clip = (Clip)AudioSystem.getLine(linfo);
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL);
            clip.open(ais);
            clip.start();
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {}
    }
    
    //there is currently no music for this game, only sound
//    private synchronized void loadMusic(final String url) {
//        URL soundURL = getClass().getClassLoader().getResource(url);
//        Line.Info linfo = new Line.Info(Clip.class);
//        try {
//            backgroundClip = (Clip)AudioSystem.getLine(linfo);
//            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL);
//            backgroundClip.open(ais);
//        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {}
//    }
    
//    private synchronized void stopMusic() { backgroundClip.stop(); }
//    private synchronized void playMusic() { backgroundClip.loop(1); }
//    private synchronized void loopMusic(int x) { backgroundClip.loop(x); }
    
    //grabs picture from file
    private BufferedImage getPic(String path) {
        BufferedImage bi = null;
        try{
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(path);
            bi = ImageIO.read(is);
        } catch (IOException e) {}
        return bi;
    }
    
    private Random gen = new Random();
    //music (currently disabled)
//    private Clip backgroundClip;
    private boolean soundOn = true;
    
    //main thread
    private Thread thread;
    private int mouseX, mouseY;
    private int oX, oY;
    private int screenX, screenY;
    //image/stage handling
    private Graphics buffer;
    
    private BufferedImage[] titles; //5
    private BufferedImage[] cursor;
    private BufferedImage[] icons;
    private BufferedImage[] enemies;
    private BufferedImage[] birds;
    private BufferedImage[] eggs;
    private BufferedImage[] weapons;
    
    
    private BufferedImage img;  //canvas
    private BufferedImage background;   //background picture
    
    private boolean gunOut;// = false;
    private int health, ammo;
    private List<projectile> shot;
    
    
    
    private boolean debug = false;
    private boolean god = false;
    private boolean networking = true;
    private int stage; //game stages
    //score
    private boolean showScores;
    private boolean ready;
    
    private String name, server;
    private int score;
    private boolean hardmode = false;
    private List<String> normScore, hardScore;
    
    private List<enemy> ene;
    private List<bird> brd;
    private List<projectile> egg;
    private List<explosion> explo;

}
//enemy/friendly class
class enemy {
    int x, y;
    //int direction; //0 = up, 1 = down, 2 = left, 3 = right;
    boolean right;
    boolean isSitting;
    boolean isGood; //determines if the enemie can be collected or not
    int delay;
    int speed;
}

class bird {
    int x, y;
    boolean right;
    int speed;
    int delay;
    int dropLocation;
    int eggtype;//0 = regular egg, 1 = ammo, 2 = hp, 3 = bomb?
}

class projectile {
    int x, y, direction, speed, type, explY;
}

class explosion {
    int x, y, radius, duration;
    boolean hurtful;
}