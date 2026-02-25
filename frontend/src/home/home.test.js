import { render, screen } from "../test-utils";
import Home from "./index";
import jwt_decode from "jwt-decode";

jest.mock("./PixelBlast", () => () => <div data-testid="pixel-blast" />);
jest.mock("jwt-decode", () => jest.fn());

describe("Home", () => {
  beforeEach(() => {
    sessionStorage.clear();
    jwt_decode.mockReset();
  });

  test("renders hero and background when logged out", () => {
    render(<Home />);

    const heading = screen.getByRole("heading", { name: /end of line/i });
    expect(heading).toBeInTheDocument();

    const background = screen.getByTestId("pixel-blast");
    expect(background).toBeInTheDocument();

    const achievements = screen.queryByText(/achievements/i);
    expect(achievements).not.toBeInTheDocument();
  });

  test("shows quick links for player", () => {
    sessionStorage.setItem("jwt", JSON.stringify("token"));
    jwt_decode.mockReturnValue({ authorities: ["PLAYER"] });

    render(<Home />);

    const achievements = screen.getByText(/achievements/i);
    expect(achievements).toBeInTheDocument();

    const friendships = screen.getByText(/friendships/i);
    expect(friendships).toBeInTheDocument();

    const puzzles = screen.getByText(/puzzles/i);
    expect(puzzles).toBeInTheDocument();
  });
});
