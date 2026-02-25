import AppNavbar from "./AppNavbar";
import { render, screen } from "./test-utils";

jest.mock("gsap", () => ({
    gsap: { fromTo: jest.fn() },
}));

describe('AppNavbar', () => {
    beforeEach(() => {
        sessionStorage.clear();
    });

    test('renders public links correctly', () => {
        render(<AppNavbar />);
        const linkDocsElement = screen.getByRole('link', { name: /docs/i });
        expect(linkDocsElement).toBeInTheDocument();

        const linkRegisterElement = screen.getByRole('link', { name: /register/i });
        expect(linkRegisterElement).toBeInTheDocument();

        const linkHomeElement = screen.getByRole('link', { name: /end of line/i });
        expect(linkHomeElement).toBeInTheDocument();
    });

    test('renders login link for guests', () => {
        render(<AppNavbar />);
        const linkLoginElement = screen.getByRole('link', { name: /login/i });
        expect(linkLoginElement).toBeInTheDocument();
    });

});
