import Datatable from "../../general/Datatable";
import { useState, useEffect } from "react";
import {
  Autocomplete,
  Avatar,
  Box,
  Button,
  Chip,
  Grid,

  Stack,
  TextField,
  Typography,
} from "@mui/material";
import BasicDialog from "../../general/BasicDialog";
import useAxios from "../../../hooks/useAxios";

export default function UpdateEmployeeTechStackDialog({ updateTechState, setUpdateTechState, employeeId }) {
  const axios = useAxios();
  const [allTech, setAllTech] = useState([]);
  const [value, setValue] = useState(null);
  const [yearOfExperience, setYearOfExperience] = useState("");
  const [techDetails, setTechDetails] = useState({ techInfo: [] });

  useEffect(() => {
    fetchTechStack();
  }, []);

  useEffect(() => {
    if (employeeId != -1)
      fetchUserTechDetails();
  }, [employeeId]);

  async function fetchUserTechDetails() {
    console.log(employeeId);
    const response = await axios.get(`/admin/techStack/${employeeId}`);
    console.log(response);
    setTechDetails(response.data);
  }

  async function fetchTechStack() {
    const response = await axios.get("/admin/techStack?size=100");
    setAllTech(response.data.content);
  }

  function build() {
    const chip = (
      <Stack key={1} spacing={2}>
        <Typography variant="h6" >
          Their tech stack
        </Typography>
        <Grid container spacing={1} sx={{ width: "600px" }}>
          {techDetails.techInfo.map((t, index) => (
            <Grid key={index} item xs={3}>
              <Chip
                label={t.techName + " " + t.yearOfExperience}
                onDelete={(e) => console.log("object")}
              />
            </Grid>
          ))}
        </Grid>
      </Stack>
    );
    const form = (
      <Stack key={2} spacing={2} mt={3} >
        <Typography variant="h6"  >
          Select tech stack
        </Typography>
        <Stack direction={"row"} spacing={1} mt={2}>
          <Autocomplete
            fullWidth
            value={value}
            onChange={(e, newValue) => handleChange(e, newValue)}
            getOptionLabel={(o) => o.name}
            isOptionEqualToValue={(option, value) => option.id === value.id}
            options={allTech}
            renderInput={(params) => <TextField {...params} label="Tech" />}
          />
          <TextField
            fullWidth
            label="Year of experience"
            value={yearOfExperience}
            onChange={(e) => setYearOfExperience(e.target.value)}
            type="number"
          />
        </Stack>
      </Stack>
    );

    const button = (
      <Stack key={3} direction={"row-reverse"} spacing={1} mt={2}>
        <Button variant="contained" size="small" onClick={handleAddTech}>
          Add
        </Button>
      </Stack>
    );

    return [chip, form, button];
  }

  function handleAddTech() {
    const newData = techDetails;

    var check = false;
    for (let index = 0; index < newData.techInfo.length; index++) {
      const v = newData.techInfo[index];
      if (v.techId === value.id) {
        newData.techInfo[index].yearOfExperience = yearOfExperience;
        check = true;
        break;
      }
    }
    if (!check) {
      newData.techInfo.push({
        techId: value.id,
        techName: value.name,
        yearOfExperience,
      });
    }

    setTechDetails(newData);
    setValue(null);
    setYearOfExperience("");
  }
  const handleChange = (event, newValue) => {
    setValue(newValue);
  };
  async function handleSendUpdate() {
    try {
      const data = {
        employeeId: techDetails.employeeId,
        techStacks: techDetails.techInfo.map((t) => ({
          techId: t.techId,
          yearOfExperience: t.yearOfExperience,
        })),
      };

      await axios.put("/admin/techStack", data);
    } catch (error) {
      throw error
    }

    setTimeout(() => {
      setUpdateTechState(false);
      setValue(null)
      setYearOfExperience('')
    }, 2000);
  }
  return (
    <>
  
        <BasicDialog
          title="Update tech stack"
          open={updateTechState}
          handleClose={(e) => setUpdateTechState(false)}
          action="update"
          content={build()}
          buttonText="Update"
          onButtonClick={handleSendUpdate}
        />

    </>
  );
}
