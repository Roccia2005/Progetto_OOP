package it.unibo.jnavy.model;

import java.util.List;

import it.unibo.jnavy.model.shots.HitStrategy;
import it.unibo.jnavy.model.utilities.Position;
import it.unibo.jnavy.model.ship.Ship;

public interface Player {
    /*
        metodo per ottenere la lista di navi(FLOTTA);
    */
    List<Ship> getShips();

    /*
        metodo con cui il player crea uno sparo, Human (standard o area), Bot(standard)

        verrà inviato a weather per modificarlo nel caso ci sia un evento atmosferico in corso
    */
    HitStrategy createShot(Position target);
}
