import { render, screen, waitFor } from "../test-utils";
import userEvent from "@testing-library/user-event";
import { rest } from "msw";
import { server } from "../mocks/server";
import PuzzleMenu from "./PuzzleMenu";

const mockNavigate = jest.fn();

jest.mock("react-router-dom", () => {
  const actual = jest.requireActual("react-router-dom");
  return { ...actual, useNavigate: () => mockNavigate };
});

describe("PuzzleMenu", () => {
  beforeEach(() => {
    sessionStorage.clear();
    sessionStorage.setItem("jwt", JSON.stringify("token"));
    mockNavigate.mockClear();
  });

  test("renders puzzles and actions", async () => {
    server.use(
      rest.get("*/api/v1/gameList/puzzle", (req, res, ctx) => {
        return res(
          ctx.status(200),
          ctx.json([
            { id: 1, name: "First Puzzle" },
            { id: 2, name: "Second Puzzle" },
          ])
        );
      })
    );

    render(<PuzzleMenu />);

    expect(await screen.findByText(/solo mode - puzzles/i)).toBeInTheDocument();
    expect(await screen.findByText("First Puzzle")).toBeInTheDocument();
    expect(await screen.findByText("Second Puzzle")).toBeInTheDocument();

    const solitaireButtons = screen.getAllByRole("button", { name: /solitaire/i });
    expect(solitaireButtons).toHaveLength(2);

    const puzzleButtons = screen.getAllByRole("button", { name: /puzzle/i });
    expect(puzzleButtons).toHaveLength(2);
  });

  test("starts puzzle and navigates to play view", async () => {
    server.use(
      rest.get("*/api/v1/gameList/puzzle", (req, res, ctx) => {
        return res(ctx.status(200), ctx.json([{ id: 3, name: "Third Puzzle" }]));
      }),
      rest.post("*/api/v1/gameList/puzzle/:id", (req, res, ctx) => {
        return res(ctx.status(200), ctx.json({ gameId: 99 }));
      })
    );

    render(<PuzzleMenu />);
    const user = userEvent.setup();

    await screen.findByText("Third Puzzle");

    const puzzleButton = screen.getByRole("button", { name: /puzzle/i });
    await user.click(puzzleButton);

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith("/gameList/99/play", {
        state: { gameStartData: { gameId: 99 } },
      });
    });
  });
});
