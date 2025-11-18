import React, { useEffect, useState } from "react";
import { getAllCustomers } from "../services/CustomerService";
import { useNavigate } from "react-router-dom";

const ListCustomersComponent = () => {
  const [customers, setCustomers] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    getAllCustomers().then((res) => {
      setCustomers(res.data);
    });
  }, []);

  function editCustomer(id) {
    navigate(`/edit-user/${id}/customer`);
  }

  return (
    <div className="container" style={{ paddingTop: "120px" }}>
      <h2 className="text-center mb-4">Customer Directory</h2>

      <table className="table table-bordered table-striped">
        <thead>
          <tr>
            <th>Username</th>
            <th>Email</th>
          </tr>
        </thead>

        <tbody>
          {customers.map((c) => (
            <tr key={c.id} onClick={() => editCustomer(c.id)} style={{ cursor: "pointer" }}>
              <td>{c.username}</td>
              <td>{c.email}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ListCustomersComponent;
