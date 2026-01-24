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
     * - enemyGrid = griglia avversaria reale (il controller passa direttamente al costruttore dello sniper la human.getGrid() alla creazione di esso)
     * - lo sniper si prende la metà sinistra e salva tutto in una lista knownTargets
     *
     * Cosa fa lo sniper bot finchè ci sono celle in knownTargets:
     * - come prima cella da colpire sceglie la prima cella occupata che incontra nella lista knownTargets;
     * - se colpisce una nave (HIT) ma non la affonda, controlla la knownTargets:
     *      1. se il resto della nave è ancora nella lista (quindi nella metà sinistra): continua a sparare a quelle coordinate.
     *      2. se il resto della nave NON è nella lista (quindi la nave attraversa il confine verso destra):
     *              a. il Bot calcola la prossima cella adiacente verso DESTRA (nella zona buia) e spara li'.
     *
     * Cosa fa lo sniper bot quando knownTargets è vuota:
     * - distrutte tutte le navi nella metà sinistra, sniperBot si comporta come proBot MA solo nella metà destra.
     *
     * Devo ricordarmi di estendere da ProBot direttamente così da avere il comportamento già pronto!!!
     */
}
