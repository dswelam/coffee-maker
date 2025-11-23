// src/test/LoginComponent.test.jsx
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import { vi } from 'vitest'
import LoginComponent from '../components/LoginComponent.jsx'

// Mock AuthService
vi.mock('../services/AuthService', () => ({
  loginAPICall: vi.fn(),
  saveLoggedInUser: vi.fn(),
  storeToken: vi.fn(),
  isAdminUser: vi.fn(() => false),
  isStaffUser: vi.fn(() => false),
  isCustomerUser: vi.fn(() => false)
}))

import { loginAPICall } from '../services/AuthService'

describe('LoginComponent', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  const renderLogin = () =>
    render(
      <MemoryRouter initialEntries={['/login']}>
        <LoginComponent />
      </MemoryRouter>
    )

  it('renders username, password fields and submit button', () => {
    renderLogin()

    expect(
      screen.getByPlaceholderText(/enter username or email/i)
    ).toBeInTheDocument()

    expect(
      screen.getByPlaceholderText(/enter password/i)
    ).toBeInTheDocument()

    // In the DOM you showed, button text is "Submit"
    expect(
      screen.getByRole('button', { name: /submit/i })
    ).toBeInTheDocument()
  })

  it('lets the user type into the login form', async () => {
    renderLogin()

    const usernameInput =
      screen.getByPlaceholderText(/enter username or email/i)
    const passwordInput =
      screen.getByPlaceholderText(/enter password/i)

    await userEvent.type(usernameInput, 'testuser')
    await userEvent.type(passwordInput, 'secret123')

    expect(usernameInput).toHaveValue('testuser')
    expect(passwordInput).toHaveValue('secret123')
  })

  it('calls loginAPICall with entered credentials when the form is submitted', async () => {
    renderLogin()

    // simulate backend failure; we only care that the call happens
    loginAPICall.mockRejectedValueOnce(new Error('fail'))

    const usernameInput =
      screen.getByPlaceholderText(/enter username or email/i)
    const passwordInput =
      screen.getByPlaceholderText(/enter password/i)

    await userEvent.type(usernameInput, 'testuser')
    await userEvent.type(passwordInput, 'secret123')

    const loginButton = screen.getByRole('button', { name: /submit/i })
    await userEvent.click(loginButton)

    expect(loginAPICall).toHaveBeenCalledTimes(1)
    expect(loginAPICall).toHaveBeenCalledWith('testuser', 'secret123')
  })
})
