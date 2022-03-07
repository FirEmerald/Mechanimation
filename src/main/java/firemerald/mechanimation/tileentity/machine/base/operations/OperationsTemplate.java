package firemerald.mechanimation.tileentity.machine.base.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OperationsTemplate<T>
{
	private final List<Function<T, Runnable>> operations = new ArrayList<>();

	public OperationsTemplate<T> addOperation(Function<T, Runnable> operation)
	{
		operations.add(operation);
		return this;
	}

	public List<Runnable> build(T machine)
	{
		return operations.stream().map(op -> op.apply(machine)).collect(Collectors.toList());
	}
}