import { render, screen } from "../test-utils";
import userEvent from "@testing-library/user-event";
import { rest } from "msw";
import { server } from "../mocks/server";
jest.mock("sockjs-client", () => jest.fn(() => ({})));

jest.mock("@stomp/stompjs", () => ({
  __esModule: true,
  Client: jest.fn(),
  Stomp: {
    over: () => ({
      connect: jest.fn((headers, onConnect) => {
        if (onConnect) onConnect();
      }),
      subscribe: jest.fn(),
      disconnect: jest.fn(),
    }),
  },
  default: {
    over: () => ({
      connect: jest.fn((headers, onConnect) => {
        if (onConnect) onConnect();
      }),
      subscribe: jest.fn(),
      disconnect: jest.fn(),
    }),
  },
}));

jest.mock("../invitation/InviteFriendModal", () => (props) => {
  if (!props.open) return null;
  return <div data-testid="invite-modal">Invite</div>;
});

const Lobby = require("./index").default;

const originalFetch = global.fetch;

const mockGame = {
  id: 123,
  name: "Battle One",
  password: "",
  host: "host1",
  gameMode: "VERSUS",
  winner: "",
  numPlayers: 2,
  isPrivate: false,
  players: [
    {
      player: { username: "host1", avatarId: 2 },
      playerColor: "BLUE",
    },
  ],
};

describe("Lobby", () => {
  beforeAll(() => {
    if (typeof originalFetch === "function") {
      global.fetch = (input, init) => {
        if (typeof input === "string" && input.startsWith("/")) {
          return originalFetch(new URL(input, "http://localhost"), init);
        }
        return originalFetch(input, init);
      };
    }
  });

  afterAll(() => {
    if (typeof originalFetch === "function") {
      global.fetch = originalFetch;
    }
  });

  beforeEach(() => {
    sessionStorage.clear();
    sessionStorage.setItem("jwt", JSON.stringify("token"));
    window.history.pushState({}, "Lobby", "/gameList/123");
    server.use(
      rest.get("*/api/v1/gameList/123", (req, res, ctx) => {
        return res(ctx.status(200), ctx.json(mockGame));
      })
    );
  });

  test("renders lobby details and disables start for non-host", async () => {
    sessionStorage.setItem("user", JSON.stringify({ id: 9, username: "guest" }));

    render(<Lobby />);

    expect(await screen.findByText(/battle one/i)).toBeInTheDocument();
    const playersLine = screen.getByText(/players:/i);
    expect(playersLine).toHaveTextContent("1/2");
    const hostLine = screen.getByText(/host:/i);
    expect(hostLine).toHaveTextContent("host1");
    expect(screen.getByText(/waiting/i)).toBeInTheDocument();

    const startButton = screen.getByRole("button", { name: /start game/i });
    expect(startButton).toBeDisabled();
  });

  test("opens invite modal on button click", async () => {
    sessionStorage.setItem("user", JSON.stringify({ id: 1, username: "host1" }));

    render(<Lobby />);
    const user = userEvent.setup();

    await screen.findByText(/battle one/i);

    const inviteButton = screen.getByRole("button", { name: /invite friend/i });
    await user.click(inviteButton);

    expect(screen.getByTestId("invite-modal")).toBeInTheDocument();
  });
});
