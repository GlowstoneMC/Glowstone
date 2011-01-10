#!/usr/bin/env ruby
Dir.glob('**/*.java').each do |name|
  lines = []
  file = File.new(name, 'r')
  begin
    while (line = file.gets) != nil
      lines << line.rstrip
    end
  ensure
    file.close
  end

  file = File.new(name, 'w')
  begin
    lines.each do |line|
      file.puts(line)
    end
  ensure
    file.close
  end
end
