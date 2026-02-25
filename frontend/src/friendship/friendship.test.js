import { render, screen, waitFor } from "../test-utils";
import userEvent from "@testing-library/user-event";
import { rest } from "msw";
import { server } from "../mocks/server";

const mockNavigate = jest.fn();

jest.mock("react-router-dom", () => {
  const actual = jest.requireActual("react-router-dom");
  return { ...actual, useNavigate: () => mockNavigate };
});

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
      connected: true,
    }),
  },
  default: {
    over: () => ({
      connect: jest.fn((headers, onConnect) => {
        if (onConnect) onConnect();
      }),
      subscribe: jest.fn(),
      disconnect: jest.fn(),
      connected: true,
    }),
  },
}));

const CreateFriendship = require("./createFriendship").default;
const FriendshipList = require("./friendshipList").default;

const originalFetch = global.fetch;

describe("Friendship flows", () => {
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
    mockNavigate.mockClear();
  });

  test("create friendship submits and navigates on success", async () => {
    sessionStorage.setItem("jwt", JSON.stringify("token"));
    sessionStorage.setItem("user", JSON.stringify({ id: 1, username: "me" }));

    server.use(
      rest.get("*/api/v1/players/username/:username", (req, res, ctx) => {
        return res(ctx.status(200), ctx.json({ id: 2, username: req.params.username }));
      }),
      rest.post("*/api/v1/friendships", (req, res, ctx) => {
        return res(ctx.status(200));
      })
    );

    render(<CreateFriendship />);
    const user = userEvent.setup();

    await user.type(screen.getByLabelText(/friend's nickname/i), "pal");
    await user.click(screen.getByRole("button", { name: /send/i }));

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith("/friendships");
    });
  });

  test("shows accepted friends and allows delete", async () => {
    sessionStorage.setItem("jwt", JSON.stringify("token"));
    sessionStorage.setItem("user", JSON.stringify({ id: 1, username: "me" }));
    const confirmSpy = jest.spyOn(window, "confirm").mockReturnValue(true);

    const accepted = {
      id: 10,
      sender: { id: 1, username: "me", avatarId: 1 },
      receiver: { id: 2, username: "pal", avatarId: 2, status: "ONLINE" },
      state: "ACCEPTED",
    };

    server.use(
      rest.get("*/api/v1/friendships/all", (req, res, ctx) => {
        return res(ctx.status(200), ctx.json([accepted]));
      })
    );

    render(<FriendshipList />);

    expect(await screen.findByRole("heading", { name: /friends/i })).toBeInTheDocument();
    expect(await screen.findByText("pal")).toBeInTheDocument();

    const deleteButton = screen.getByRole("button", { name: `delete-${accepted.id}` });
    const user = userEvent.setup();
    await user.click(deleteButton);

    expect(
      await screen.findByText(/you don't have any friends yet/i)
    ).toBeInTheDocument();

    confirmSpy.mockRestore();
  });

  test("switches to pending and accepts invitation", async () => {
    sessionStorage.setItem("jwt", JSON.stringify("token"));
    sessionStorage.setItem("user", JSON.stringify({ id: 1, username: "me" }));

    const pending = {
      id: 11,
      sender: { id: 3, username: "other", avatarId: 5 },
      receiver: { id: 1, username: "me", avatarId: 1 },
      state: "PENDING",
    };

    server.use(
      rest.get("*/api/v1/friendships/all", (req, res, ctx) => {
        return res(ctx.status(200), ctx.json([pending]));
      }),
      rest.put("*/api/v1/friendships/:id", (req, res, ctx) => {
        return res(ctx.status(200), ctx.json({}));
      })
    );

    render(<FriendshipList />);

    const toggleButton = await screen.findByRole("button", { name: /pending/i });
    const user = userEvent.setup();
    await user.click(toggleButton);

    expect(screen.getByRole("heading", { name: /pending invitations/i })).toBeInTheDocument();
    expect(await screen.findByText("other")).toBeInTheDocument();

    const acceptButton = screen.getByRole("button", { name: `update-${pending.id}` });
    await user.click(acceptButton);

    expect(await screen.findByText(/you don't have any pending invitations/i)).toBeInTheDocument();
  });
});
