import { render, screen } from "../../test-utils";
import Login from "./index";

describe("Login", () => {
  test("renders login form", () => {
    render(<Login />);

    const heading = screen.getByRole("heading", { name: /login/i });
    expect(heading).toBeInTheDocument();

    const usernameInput = screen.getByLabelText(/username/i);
    expect(usernameInput).toBeInTheDocument();

    const passwordInput = screen.getByLabelText(/password/i);
    expect(passwordInput).toBeInTheDocument();

    const submitButton = screen.getByRole("button", { name: /login/i });
    expect(submitButton).toBeInTheDocument();
  });
});
