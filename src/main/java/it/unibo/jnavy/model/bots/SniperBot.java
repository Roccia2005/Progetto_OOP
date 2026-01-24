package it.unibo.jnavy.model.bots;

import it.unibo.jnavy.model.HitType;
import it.unibo.jnavy.model.grid.Grid;
import it.unibo.jnavy.model.utilities.Position;

public class SniperBot implements BotStrategy{

    @Override
    public Position selectTarget(final Grid enemyGrid) {

        return new Position();
    }

    @Override
    public void lastShotFeedback(final Position target, final HitType result) {

    }

    /**
     * Cosa viene fornito allo sniper bot:
     * - halfGrid = metà sinistra della griglia avversaria
     * Cosa fa lo sniper bot:
     * - prima di tutto fonde la halfGrid nella enemyGrid = da adesso la enemyGrid sarà una fusione tra enemyGrid e halfGrid!
     * - il primo sparo non lo fa a caso!
     * - come prima cella da colpire sceglie la prima cella occupata che incontra nella enemyGrid
     * - per le celle seguenti avrà due possibilità (come obiettivo deve avere quello di affondare):
     *      1. se nella enemyGrid è presente anche la cella adiacente a quella appena colpita ---> sniper bot bara e la colpisce direttamente;
     *      2. se nella enemyGrid non è presente la cella adiacente a quella appena colpita, si hanno due possibilità:
     *          a. la cella è presente nella enemyGrid ma si trova dall'altro lato rispetto a quella appena colpita, ad es: O X X, dobbiamo distruggere O;
     *          b. la cella non è presente nella enemyGrid quindi andrà cercata con il metodo dell'adiacenza usato nel ProBot ma in partenza andranno escluse le adiacenti vuote.
     * - nei turni successivi dato che il bot avrà nella enemyGrid eventualmente anche celle che ha colpito e quindi rivelato che non erano nella halfGrid, potrà partire da quelle usando il solito metodo di adiacenza.
     */
}
