import { render, screen } from "../test-utils";
import { rest } from "msw";
import { server } from "../mocks/server";
import GameList from "./index";

describe("GameList", () => {
  test("renders empty state when there are no games", async () => {
    server.use(
      rest.get("*/api/v1/gameList/active", (req, res, ctx) => {
        return res(ctx.status(200), ctx.json([]));
      }),
      rest.get("*/api/v1/gameList/finished", (req, res, ctx) => {
        return res(ctx.status(200), ctx.json([]));
      })
    );

    render(<GameList />);

    expect(await screen.findByText(/no active games found/i)).toBeInTheDocument();
    expect(await screen.findByText(/no finished games found/i)).toBeInTheDocument();
  });

  test("renders active and finished games tables", async () => {
    const activeGames = [
      {
        name: "Alpha Match",
        host: { username: "host1" },
        players: [{ player: { username: "p1" }, playerColor: "BLUE" }],
        gameMode: "VERSUS",
        state: "WAITING",
      },
    ];
    const finishedGames = [
      {
        name: "Omega Match",
        host: { username: "host2" },
        players: [{ player: { username: "p2" }, playerColor: "RED" }],
        gameMode: "SOLITAIRE",
        winner: "host2",
        duration_formatted: "05:10",
      },
    ];

    server.use(
      rest.get("*/api/v1/gameList/active", (req, res, ctx) => {
        return res(ctx.status(200), ctx.json(activeGames));
      }),
      rest.get("*/api/v1/gameList/finished", (req, res, ctx) => {
        return res(ctx.status(200), ctx.json(finishedGames));
      })
    );

    render(<GameList />);

    expect(await screen.findByText("Alpha Match")).toBeInTheDocument();
    expect(await screen.findByText("Omega Match")).toBeInTheDocument();

    expect(screen.getByText("host1")).toBeInTheDocument();
    const host2Entries = screen.getAllByText("host2");
    expect(host2Entries.length).toBeGreaterThan(0);

    expect(screen.getByText("p1")).toBeInTheDocument();
    expect(screen.getByText("p2")).toBeInTheDocument();

    expect(screen.getByText("VERSUS")).toBeInTheDocument();
    expect(screen.getByText("SOLITAIRE")).toBeInTheDocument();

    expect(screen.getByText("05:10")).toBeInTheDocument();
  });
});
