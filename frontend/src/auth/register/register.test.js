import { render, screen } from "../../test-utils";
import Register from "./index";

describe("Register", () => {
  test("renders user type selection", () => {
    render(<Register />);

    const heading = screen.getByRole("heading", { name: /register/i });
    expect(heading).toBeInTheDocument();

    const helperText = screen.getByText(/what type of user/i);
    expect(helperText).toBeInTheDocument();

    const playerButton = screen.getByRole("button", { name: /player/i });
    expect(playerButton).toBeInTheDocument();

    const adminButton = screen.getByRole("button", { name: /admin/i });
    expect(adminButton).toBeInTheDocument();
  });
});
