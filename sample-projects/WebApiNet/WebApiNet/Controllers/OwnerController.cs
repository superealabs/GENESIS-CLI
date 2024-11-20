namespace WebApiNet.Controllers;

using Microsoft.AspNetCore.Mvc;
using Models;
using Services.Interface;

[ApiController]
[Route("owners")]
public class OwnerController  : ControllerBase 
{
	private readonly IOwnerService _service;

	public OwnerController(IOwnerService service)
	{
		_service = service;
	}

	[HttpGet]
	public async Task<ActionResult<IEnumerable<Owner>>> GetOwners()
	{
		var owners = await _service.GetAllAsync();
		return owners.Any() ? Ok(owners) : NoContent();
	}

	[HttpGet("{id}")]
	public async Task<ActionResult<Owner>> GetOwner(int id)
	{
		var owner = await _service.GetByIdAsync(id);
		return owner == null ? NotFound() : Ok(owner);
	}

	[HttpPost]
	public async Task<ActionResult<Owner>> Post(Owner owner)
	{
		await _service.AddAsync(owner);
		return CreatedAtAction(nameof(GetOwner), new { id = owner.OwnerId }, owner);
	}

	[HttpPut("{id}")]
	public async Task<ActionResult<Owner>> PutOwner(int id, Owner owner)
	{
		if (id != owner.OwnerId)
			return BadRequest();

		var exists = await _service.ExistsAsync(id);
		if (!exists) return NotFound();

		await _service.UpdateAsync(owner);
		return NoContent();
	}

	[HttpDelete("{id}")]
	public async Task<ActionResult> DeleteOwner(int id)
	{

		var exists = await _service.ExistsAsync(id);
		if (!exists) return NotFound();

		await _service.DeleteAsync(id);
		return NoContent();
	}

}
