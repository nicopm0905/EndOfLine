import { render, screen, waitFor } from "../../test-utils";
import Spectate from "../../spectate/index";
import { server } from "../../mocks/server";
import { rest } from "msw";
import * as router from "react-router-dom";


jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useParams: () => ({ id: "1" }),
}));

jest.mock("../../gameplay/gameBoard/GameBoardComponent", () => () => <div data-testid="game-board">Game Board Mock</div>);
jest.mock("../../gameplay/playerControlsArea/playerHand/playerHand", () => () => <div data-testid="player-hand">Hand Mock</div>);
jest.mock("../../gameplay/chatMessage", () => () => <div data-testid="chat-message">Chat Mock</div>);

window.HTMLElement.prototype.scrollIntoView = jest.fn();

describe("Spectate Component", () => {

    test("Shows 'Loading...' initially and then loads game data", async () => {
        server.use(
            rest.get("*/api/v1/gameList/1/spectate", (req, res, ctx) => {
                return res(
                    ctx.status(200),
                    ctx.json({
                        id: 1,
                        gameName: "Test Game",
                        gameMode: "PUZZLE_SINGLE",
                        state: "IN_PROGRESS",
                        round: 5,
                        startTime: new Date().toISOString(),
                        players: [
                            { id: 10, playerColor: "RED", player: { username: "Player1" } }
                        ],
                        boardSize: 5,
                        gamePlayerTurnId: 10
                    })
                );
            })
        );
        render(<Spectate />);
        expect(screen.getByText(/Loading.../i)).toBeInTheDocument();
        await waitFor(() => {
            expect(screen.getByText("Test Game")).toBeInTheDocument();
            expect(screen.getByText("Mode: PUZZLE_SINGLE")).toBeInTheDocument();
            expect(screen.getByText("Round")).toBeInTheDocument();
        });
    });
    test("Shows error if request fails", async () => {
        server.use(
            rest.get("*/api/v1/gameList/1/spectate", (req, res, ctx) => {
                return res(ctx.status(500), ctx.text("Internal error"));
            })
        );
        render(<Spectate />);
        await waitFor(() => {
            expect(screen.getByText("Internal error")).toBeInTheDocument();
        });
    });
});