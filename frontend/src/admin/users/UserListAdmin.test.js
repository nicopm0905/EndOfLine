import { render, screen } from "../../test-utils";
import userEvent from "@testing-library/user-event";
import UserListAdmin from "./UserListAdmin";

describe('UserListAdmin', () => {
    test('renders users correctly', async () => {
        render(<UserListAdmin />);
        const heading = await screen.findByRole('heading', { name: /manage users/i });
        expect(heading).toBeInTheDocument();

        const newUserLink = screen.getByRole('link', { name: /\+ new user/i });
        expect(newUserLink).toBeInTheDocument();

        const totalUsers = await screen.findByText(/total users:\s*2/i);
        expect(totalUsers).toBeInTheDocument();

        const admin1 = await screen.findByRole('cell', { 'name': 'admin1' });
        expect(admin1).toBeInTheDocument();

        const owner1 = await screen.findByRole('cell', { 'name': 'owner1' });
        expect(owner1).toBeInTheDocument();

        const editButtons = await screen.findAllByRole('link', { 'name': /edit/ });
        expect(editButtons).toHaveLength(2);

        const deleteButtons = await screen.findAllByRole('button', { 'name': /delete/ });
        expect(deleteButtons).toHaveLength(2);

        const owners = await screen.findAllByRole('row', {},);
        expect(owners).toHaveLength(3);
    });

    test('delete user correct', async () => {
        const user = userEvent.setup();
        const confirmSpy = jest.spyOn(window, "confirm").mockReturnValue(true);
        render(<UserListAdmin />);

        const user1Delete = await screen.findByRole('button', { 'name': 'delete-admin1' });
        await user.click(user1Delete);
        const alert = await screen.findByRole('alert');
        expect(alert).toBeInTheDocument();

        confirmSpy.mockRestore();
    });
});
