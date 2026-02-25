import { render, screen } from "../test-utils";
import { rest } from "msw";
import { server } from "../mocks/server";
import DeveloperList from "./index";

const mockDevelopers = [
  {
    id: 1,
    name: "Alvaro",
    email: "alvaro@example.com",
    url: "https://example.com/alvaro",
    properties: { picUrl: "https://images.example.com/alvaro.png" },
  },
  {
    id: 2,
    name: "Carmen",
    email: "carmen@example.com",
    url: "https://example.com/carmen",
    properties: {},
  },
];

describe("DeveloperList", () => {
  beforeEach(() => {
    server.use(
      rest.get("*/api/v1/developers", (req, res, ctx) => {
        return res(ctx.status(200), ctx.json(mockDevelopers));
      })
    );
  });

  test("renders developer table with data", async () => {
    render(<DeveloperList />);

    const heading = screen.getByRole("heading", { name: /developers/i });
    expect(heading).toBeInTheDocument();

    const count = await screen.findByText(/team members:\s*2/i);
    expect(count).toBeInTheDocument();

    const alvaro = screen.getByText("Alvaro");
    expect(alvaro).toBeInTheDocument();

    const alvaroLink = screen.getByRole("link", {
      name: "https://example.com/alvaro",
    });
    expect(alvaroLink).toHaveAttribute("href", "https://example.com/alvaro");

    const carmenAvatar = screen.getByAltText("Carmen");
    expect(carmenAvatar.getAttribute("src")).toContain("48639.png");
  });
});
