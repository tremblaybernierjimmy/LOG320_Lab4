package Laboratoire4;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Evaluation {
    final static double MOBILITY_COEFFICIENT = 6;
    final static double CENTRALISATION_COEFFICIENT = 4;

    //////////////////
    // MOBILITY CONST
    /////////////////
    final static double CAPTURE_MODIFIER = 2.0;
    final static double MOVE_VALUE = 1.0;
    final static double EDGE_COEFFICIENT = 0.5;

    /**
     * TODO Il faut référencer proprement le code ici pour ne pas perdre de points
     * https://dke.maastrichtuniversity.nl/m.winands/documents/informed_search.pdf P.22
     */
    final static int[][] WEIGHT_MATRIX = {
            /*      1     2       3       4       5       6       7       8  */
            /*A*/ {-80,   -25,    -20,    -20,    -20,    -20,    -25,    -80},
            /*B*/ {-25,   10,     10,     10,     10,     10,     10,     -25},
            /*C*/ {-20,   10,     25,     25,     25,     25,     10,     -20},
            /*D*/ {-20,   10,     25,     50,     50,     25,     10,     -20},
            /*E*/ {-20,   10,     25,     50,     50,     25,     10,     -20},
            /*F*/ {-20,   10,     25,     25,     25,     25,     10,     -20},
            /*G*/ {-25,   10,     10,     10,     10,     10,     10,     -25},
            /*H*/ {-80,   -25,    -20,    -20,    -20,    -20,    -25,    -80},
    };


    public static double evaluateBoard(Board board) {
        return smartEvaluateBoard(board);
    }

    private static double naiveEvaluateBoard(Board board) {
        return (int) evaluateCentralisation(board);
    }

    private static double smartEvaluateBoard(Board board) {
        double playerScore = 0;

        boolean isWinning = isPlayerWinning(board);
        boolean isLosing = isEnnemyWinning(board);

        if(isWinning && isLosing) {
            return 0; // DRAW
        }

        if(isWinning) {
            return Double.MAX_VALUE;
        }

        if (isLosing) {
            return Double.MAX_VALUE;
        }

        playerScore += evaluateMobility(board) * MOBILITY_COEFFICIENT;
        playerScore += evaluateCentralisation(board) * CENTRALISATION_COEFFICIENT;

        return playerScore;
    }

    public static double evaluateMobility(Board board) {
        ArrayList<String> allPlayerMove = Movement.generateAllPossibleMoves(board, board.getPlayerColor().getValue());
        ArrayList<String> allEnnemyMove = Movement.generateAllPossibleMoves(board, board.getEnnemyColor().getValue());

        double maxPossibleValue = Math.max(allPlayerMove.size(), allEnnemyMove.size()) * CAPTURE_MODIFIER;
        double playerMobilityValue = getMovesValue(board, allPlayerMove, board.getPlayerColor());
        double ennemyMobilityValue = getMovesValue(board, allEnnemyMove, board.getEnnemyColor());


        return (playerMobilityValue - ennemyMobilityValue) / maxPossibleValue;
    }

    private static double getMovesValue(Board board, ArrayList<String> moves, Pion.colors color) {
        Case currentCase;
        int x, y;
        double moveValue;
        double  mobilityValue = 0;

        for(String move : moves) {
            moveValue = MOVE_VALUE;
            int[] position = Movement.getPosFromString(move.substring(2));
            x = position[0];
            y = position[1];

            currentCase = board.getCase(x, y);
            if(!currentCase.isEmpty() && currentCase.getPion().getColor() != color) {
                moveValue *= CAPTURE_MODIFIER;
            }

            if(isOnEdge(x, y)) {
                moveValue *= EDGE_COEFFICIENT;
            }

            mobilityValue += moveValue;
        }
        return mobilityValue;
    }

    public static double evaluateCentralisation(Board board) {
        AtomicInteger playerScore = new AtomicInteger();
        AtomicInteger ennemyScore = new AtomicInteger();

        final double MAX_POSSIBLE_VALUE = 4 * 50 + 8 * 25;

        board.getPlayerPions().forEach(pion -> {
            playerScore.addAndGet(WEIGHT_MATRIX[pion.getX()][pion.getY()]);
        });

        board.getEnnemyPions().forEach(pion -> {
            ennemyScore.addAndGet(WEIGHT_MATRIX[pion.getX()][pion.getY()]);
        });

        return (playerScore.get() - ennemyScore.get()) / MAX_POSSIBLE_VALUE;
    }


    private static boolean isOnEdge(int x, int y) {
        final int MIN = 0;
        final int MAX = 7;

        return (x == MIN || x == MAX) || (y == MIN || y == MAX);
    }

    private static boolean isPlayerWinning(Board board) {
        for (Pion pion : board.getPlayerPions()) {
            if(!isPionConnectedToSameColor(board, pion)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isEnnemyWinning(Board board) {
        for (Pion pion : board.getEnnemyPions()) {
            if(!isPionConnectedToSameColor(board, pion)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPionConnectedToSameColor(Board board, Pion pion) {
        for (int x = -1; x <= 1; x++) {
            for(int y = -1; y <= 1; y++) {
                if(x == 0 && y == 0) {
                    continue; // don't evaluate itself
                }

                int caseX = x + pion.getX();
                int caseY = y + pion.getY();

                if(!Board.inBound(caseX, 0 ,7) || !Board.inBound(caseY, 0, 7)) {
                    continue;
                }

                if(
                        !board.getCase(caseX, caseY).isEmpty()
                                && board.getCase(caseX, caseY).getPion().getColor() == board.getPlayerColor()
                ) {
                    return true;
                }
            }
        }
        return false;
    }
}
