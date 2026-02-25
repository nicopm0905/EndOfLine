import { render, screen, within } from "../test-utils";
import userEvent from "@testing-library/user-event";
import { rest } from "msw";
import { server } from "../mocks/server";
import StatisticsList from "./StatisticsList";
import StatisticGeneral from "./StatisticGeneral";

describe("Statistics", () => {
  beforeEach(() => {
    sessionStorage.clear();
    sessionStorage.setItem("user", JSON.stringify({ id: 7, username: "Neo" }));
  });

  test("statistics list shows player metrics", async () => {
    const stats = {
      gamesPlayed: 8,
      victories: 5,
      maxLineLength: 12,
      totalPlayTime: 440,
      usedCards: 80,
      averageDuration: 55,
      powerMostUsed: "BOOST",
      higuestScorePuzzle: 90,
      completedPuzzle: 3,
    };

    server.use(
      rest.get("*/api/v1/statistics/player/:id", (req, res, ctx) => {
        return res(ctx.status(200), ctx.json(stats));
      })
    );

    render(<StatisticsList />);

    const gamesPlayedCard = await screen.findByText(/games played/i);
    const gamesPlayedContainer = gamesPlayedCard.closest(".stat-card");
    expect(await within(gamesPlayedContainer).findByText("8")).toBeInTheDocument();

    const puzzleCard = screen.getByText(/completed puzzle/i);
    const puzzleContainer = puzzleCard.closest(".stat-card");
    expect(await within(puzzleContainer).findByText("3")).toBeInTheDocument();
  });

  test("statistic general renders summary and search modal", async () => {
    const stats = { gamesPlayed: 10, victories: 6 };
    const rankings = {
      VICTORIES: [{ playerId: 1, username: "Alpha", value: 12 }],
      TOTAL_PLAY_TIME: [{ playerId: 2, username: "Beta", value: 900 }],
      TOTAL_SCORE: [{ playerId: 3, username: "Gamma", value: 3000 }],
    };

    server.use(
      rest.get("*/api/v1/statistics/player/:id", (req, res, ctx) => {
        return res(ctx.status(200), ctx.json(stats));
      }),
      rest.get("*/api/v1/statistics/ranking", (req, res, ctx) => {
        const metric = req.url.searchParams.get("metric");
        return res(ctx.status(200), ctx.json(rankings[metric] || []));
      })
    );

    render(<StatisticGeneral />);

    expect(await screen.findByText("Neo")).toBeInTheDocument();
    expect(await screen.findByText(/10 games/i)).toBeInTheDocument();
    expect(await screen.findByText(/6 victories/i)).toBeInTheDocument();
    expect(await screen.findByText(/top victories/i)).toBeInTheDocument();
    expect(await screen.findByText("Alpha")).toBeInTheDocument();

    const user = userEvent.setup();
    const searchButton = screen.getByRole("button", { name: /search rankings/i });
    await user.click(searchButton);

    const searchInput = screen.getByPlaceholderText(/search metrics/i);
    expect(searchInput).toBeInTheDocument();

    await user.type(searchInput, "victories");
    const victoryMatches = screen.getAllByText(/victories/i);
    expect(victoryMatches.length).toBeGreaterThan(0);
  });
});
