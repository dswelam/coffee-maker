// src/test/App.test.jsx
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import App from '../App.jsx'

describe('Login page', () => {
  it('renders navbar, login form, and footer', () => {
    render(<App />)

    // Navbar brand link
    expect(
      screen.getByRole('link', { name: /WolfCafe/i })
    ).toBeInTheDocument()

    // Login form heading
	expect(screen.getByRole('heading', { name: /welcome/i })).toBeInTheDocument()

    // Inputs
    expect(
      screen.getByPlaceholderText(/Enter username or email/i)
    ).toBeInTheDocument()
    expect(
      screen.getByPlaceholderText(/Enter password/i)
    ).toBeInTheDocument()

    // Submit button
    expect(
      screen.getByRole('button', { name: /login/i })
    ).toBeInTheDocument()

    // Footer
    expect(
      screen.getByText(/WolfCafe © 2025/i)
    ).toBeInTheDocument()
  })

  it('lets the user type into the login form', async () => {
    render(<App />)

    const usernameInput = screen.getByPlaceholderText(/Enter username or email/i)
    const passwordInput = screen.getByPlaceholderText(/Enter password/i)

    await userEvent.type(usernameInput, 'testuser')
    await userEvent.type(passwordInput, 'secret123')

    expect(usernameInput).toHaveValue('testuser')
    expect(passwordInput).toHaveValue('secret123')
  })

  it('does not show an error message before submit', () => {
    render(<App />)

    // Example of queryBy* (from the tutorial’s "search variants" section)
    expect(
      screen.queryByText(/invalid username or password/i)
    ).toBeNull()
  })
})
