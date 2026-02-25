import { render, screen } from "../test-utils";
import userEvent from "@testing-library/user-event";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { rest } from "msw";
import { server } from "../mocks/server";
import useIntervalFetchState from "../util/useIntervalFetchState";
import Spectate from "./index";
import SpectateView from "./SpectateView";

jest.mock("../gameplay/gameBoard/GameBoardComponent", () => () => (
  <div data-testid="game-board" />
));
jest.mock("../gameplay/playerControlsArea/playerHand/playerHand", () => () => (
  <div data-testid="player-hand" />
));
jest.mock("../gameplay/chatMessage", () => () => <div data-testid="chat-message" />);

const mockNavigate = jest.fn();
jest.mock("react-router-dom", () => {
  const actual = jest.requireActual("react-router-dom");
  return { ...actual, useNavigate: () => mockNavigate };
});

jest.mock("../util/useIntervalFetchState", () => jest.fn());

describe("Spectate", () => {
  beforeEach(() => {
    sessionStorage.clear();
    sessionStorage.setItem("jwt", JSON.stringify("token"));
    mockNavigate.mockClear();
    useIntervalFetchState.mockReset();
  });

  test("spectate view shows empty state", () => {
    useIntervalFetchState.mockReturnValue([[], jest.fn()]);

    render(<SpectateView />);

    expect(screen.getByText(/no active games to spectate/i)).toBeInTheDocument();
  });

  test("spectate view lists games and navigates", async () => {
    useIntervalFetchState.mockReturnValue([
      [
        {
          id: 7,
          name: "Match One",
          host: "host1",
          players: [{ id: 1 }, { id: 2 }],
          numPlayers: 4,
          gameMode: "VERSUS",
        },
      ],
      jest.fn(),
    ]);

    render(<SpectateView />);
    const user = userEvent.setup();

    expect(screen.getByText("VERSUS")).toBeInTheDocument();
    expect(screen.getByText("Match One")).toBeInTheDocument();

    const spectateButton = screen.getByRole("button", { name: /spectate/i });
    await user.click(spectateButton);

    expect(mockNavigate).toHaveBeenCalledWith("/spectate/7");
  });

  test("spectate screen renders game details and toggles hand", async () => {
    const game = {
      id: 7,
      gameName: "Showdown",
      gameMode: "VERSUS",
      state: "ACTIVE",
      round: 2,
      boardSize: 5,
      winner: null,
      startTime: null,
      gamePlayerTurnId: 1,
      players: [
        { id: 1, player: { username: "Alice" }, playerColor: "BLUE", energy: 3 },
        { id: 2, player: { username: "Bob" }, playerColor: "RED", energy: 2 },
      ],
      placedCards: [],
    };

    server.use(
      rest.get("*/api/v1/gameList/:id/spectate", (req, res, ctx) => {
        return res(ctx.status(200), ctx.json(game));
      }),
      rest.get("*/api/v1/cards/player-hand/:pgsId", (req, res, ctx) => {
        return res(
          ctx.status(200),
          ctx.json([{ id: 1, name: "Card A" }])
        );
      })
    );

    const wrapper = ({ children }) => (
      <MemoryRouter initialEntries={["/spectate/7"]}>{children}</MemoryRouter>
    );

    render(
      <Routes>
        <Route path="/spectate/:id" element={<Spectate />} />
      </Routes>,
      { wrapper }
    );

    expect(await screen.findByText(/showdown/i)).toBeInTheDocument();
    expect(screen.getByText(/mode:\s*versus/i)).toBeInTheDocument();
    expect(screen.getByTestId("game-board")).toBeInTheDocument();
    expect(screen.getByTestId("chat-message")).toBeInTheDocument();

    const user = userEvent.setup();
    const toggleButton = screen.getAllByRole("button", { name: /show hand/i })[0];
    await user.click(toggleButton);

    expect(await screen.findByTestId("player-hand")).toBeInTheDocument();
  });
});
