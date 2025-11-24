// src/test/HeaderComponent.test.jsx
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import userEvent from '@testing-library/user-event'
import { vi } from 'vitest'
import HeaderComponent from '../components/HeaderComponent.jsx'

vi.mock('../services/AuthService', () => ({
  isAdminUser: vi.fn(),
  isStaffUser: vi.fn(),
  isCustomerUser: vi.fn(),
  isUserLoggedIn: vi.fn(),
  logout: vi.fn()
}))

import {
  isAdminUser,
  isStaffUser,
  isCustomerUser,
  isUserLoggedIn,
  logout
} from '../services/AuthService'

describe('HeaderComponent', () => {
  beforeEach(() => {
    vi.resetAllMocks()
  })

  const renderHeader = () =>
    render(
      <MemoryRouter>
        <HeaderComponent />
      </MemoryRouter>
    )

  it('shows brand and Login/Register when user is not authenticated', () => {
    isUserLoggedIn.mockReturnValue(false)
    isAdminUser.mockReturnValue(false)
    isStaffUser.mockReturnValue(false)
    isCustomerUser.mockReturnValue(false)

    renderHeader()

    // brand
    expect(
      screen.getByRole('link', { name: /wolfcafe/i })
    ).toBeInTheDocument()

    // guest links
    expect(
      screen.getByRole('link', { name: /register/i })
    ).toBeInTheDocument()
    expect(
      screen.getByRole('link', { name: /^login$/i })
    ).toBeInTheDocument()

    // no logout / items when logged out
    expect(screen.queryByText(/logout/i)).toBeNull()
    expect(screen.queryByText(/items/i)).toBeNull()
  })

  it('shows Items and Logout when logged in as admin (no Login/Register)', () => {
    isUserLoggedIn.mockReturnValue(true)
    isAdminUser.mockReturnValue(true)
    isStaffUser.mockReturnValue(false)
    isCustomerUser.mockReturnValue(false)

    renderHeader()

    // brand
    expect(
      screen.getByRole('link', { name: /wolfcafe/i })
    ).toBeInTheDocument()

    // logged-in nav
    expect(
      screen.getByRole('link', { name: /items/i })
    ).toBeInTheDocument()
    expect(
      screen.getByRole('link', { name: /logout/i })
    ).toBeInTheDocument()

    // guest links hidden
    expect(screen.queryByRole('link', { name: /^login$/i })).toBeNull()
    expect(screen.queryByRole('link', { name: /register/i })).toBeNull()
  })

  it('shows Order and My Orders for logged-in customer', async () => {
      isUserLoggedIn.mockReturnValue(true)
      isAdminUser.mockReturnValue(false)
      isStaffUser.mockReturnValue(false)
      isCustomerUser.mockReturnValue(true)

      renderHeader()

	  // shared logged-in links
	  expect(screen.getByRole('link', { name: /^Order$/i })).toBeInTheDocument()
	  expect(screen.getByRole('link', { name: /^My Orders$/i })).toBeInTheDocument()

	  const logoutLink = screen.getByRole('link', { name: /logout/i })
	  expect(logoutLink).toBeInTheDocument()

	  // guest links hidden
	  expect(screen.queryByRole('link', { name: /login/i })).toBeNull()
	  expect(screen.queryByRole('link', { name: /register/i })).toBeNull()

	  // clicking Logout calls logout()
	  await userEvent.click(logoutLink)
	  expect(logout).toHaveBeenCalled()
    })
})
