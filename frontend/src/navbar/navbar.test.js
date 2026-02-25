import { render, screen } from "../test-utils";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import CardNav from "./CardNav";

jest.mock("gsap", () => ({
  gsap: { fromTo: jest.fn() },
}));

const items = [
  { key: "home", label: "Home", to: "/home" },
  { key: "docs", label: "Docs", to: "/docs", position: "right" },
];

describe("CardNav", () => {
  test("renders public nav and highlights active item", () => {
    const wrapper = ({ children }) => (
      <MemoryRouter initialEntries={["/home"]}>{children}</MemoryRouter>
    );

    render(
      <CardNav
        items={items}
        username=""
        avatarSrc="/avatars/avatar1.png"
        onAvatarClick={() => {}}
        isAuthenticated={false}
        unreadCount={0}
        onNotificationClick={() => {}}
      />,
      { wrapper }
    );

    const brand = screen.getByRole("link", { name: /end of line/i });
    expect(brand).toBeInTheDocument();

    const login = screen.getByRole("link", { name: /login/i });
    expect(login).toBeInTheDocument();

    const active = screen.getByRole("link", { name: /home/i });
    expect(active.className).toContain("card-nav__item--active");
  });

  test("renders avatar, badge, and handles clicks", async () => {
    const wrapper = ({ children }) => (
      <MemoryRouter initialEntries={["/"]}>{children}</MemoryRouter>
    );
    const onAvatarClick = jest.fn();
    const onNotificationClick = jest.fn();
    const user = userEvent.setup();

    render(
      <CardNav
        items={items}
        username="player1"
        avatarSrc="/avatars/avatar2.png"
        onAvatarClick={onAvatarClick}
        isAuthenticated={true}
        unreadCount={3}
        onNotificationClick={onNotificationClick}
      />,
      { wrapper }
    );

    const avatarButton = screen.getByRole("button", { name: /abrir perfil/i });
    await user.click(avatarButton);
    expect(onAvatarClick).toHaveBeenCalled();

    const badge = screen.getByText("3");
    expect(badge).toBeInTheDocument();

    const svgIcon = document.querySelector("svg");
    await user.click(svgIcon);
    expect(onNotificationClick).toHaveBeenCalled();
  });
});
