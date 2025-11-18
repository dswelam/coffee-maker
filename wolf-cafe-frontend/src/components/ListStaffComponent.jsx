import React, { useEffect, useState } from "react";
import { getAllStaff } from "../services/StaffService";
import { useNavigate } from "react-router-dom";

const ListStaffComponent = () => {
  const [staff, setStaff] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    getAllStaff().then((res) => {
      setStaff(res.data);
    });
  }, []);

  function editStaff(id) {
    navigate(`/edit-user/${id}/staff`);
  }

  return (
    <div className="container" style={{ paddingTop: "120px" }}>
      <h2 className="text-center mb-4">Staff Directory</h2>

      <table className="table table-bordered table-striped">
        <thead>
          <tr>
            <th>Username</th>
            <th>Email</th>
            <th>Role</th>
          </tr>
        </thead>

        <tbody>
          {staff.map((s) => (
            <tr key={s.id} onClick={() => editStaff(s.id)} style={{ cursor: "pointer" }}>
              <td>{s.username}</td>
              <td>{s.email}</td>
              <td>{s.role}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ListStaffComponent;
